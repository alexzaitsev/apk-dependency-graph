
const { ipcMain, ipcRenderer } = require('electron')
const ApkDependencyGraphRunner = require('../runner/apk-dependency-graph-runner')
const EventBus = require('../util/event-bus')

const ACTION_RUN_APK_DEPENDENCY_GRAPH = 'run-apk-dependency-graph'
const ACTION_FAIL_APK_DEPENDENCY_GRAPH = 'fail-on-loading-apk'
const ACTION_OPEN_VIEWER_PAGE = 'open-viewer-page'

function throwIfIsNotMainProcess() {
    if (process && process.type !== 'browser') {
        throwException('Function cannot be called outside the Main process!')
    }
}

function throwIfIsNotRendererProcess() {
    if (process && process.type !== 'renderer') {
        throwException('Function cannot be called outside the Renderer process!')
    }
}

function throwException(message) {
    throw { name: "Exception", message: message }
}

/**
 *  It is responsible for listening to the action for running the APK
 *  dependency analysis in the Main process, and this action must be
 *  triggered in the Renderer process.
 *
 *  Obs.: The long processes should be executed in the Main process
 *  instead of the Renderer process.
 */
class ApkDependencyController {
    constructor(appPath) {
        this.runner = new ApkDependencyGraphRunner(appPath)
    }

    /**
     * Internally register to listen the apk dependency analysis action from
     * the Renderer process.
     */
    start() {
        throwIfIsNotMainProcess()
        ipcMain.on(ACTION_RUN_APK_DEPENDENCY_GRAPH,
                this.runDependencyGraph.bind(this))
    }

    /** Unregister all listerners */
    stop() {
        throwIfIsNotMainProcess()
        ipcMain.removeAllListeners(ACTION_RUN_APK_DEPENDENCY_GRAPH)
    }

    runDependencyGraph(event, config) {
        this.runner.run(config)
            .then(() => {
                console.log("completed!")
                EventBus.send(ACTION_OPEN_VIEWER_PAGE)
            })
            .catch((error) => {
                console.log('exec error: ' + error)
                event.sender.send(ACTION_FAIL_APK_DEPENDENCY_GRAPH, error)
            })
    }
}

/**
 *  It is responsible for triggering the APK analyzing action that must run
 *  in the Electron Main process. For this action worked well it is
 *  necessary to start the controller in the Main process for listening
 *  to this action to run it there.
 * */
class ApkDependencyAnalyzer {
    analyze(config) {
        return new Promise((resolve, reject) => {
            throwIfIsNotRendererProcess()

            ipcRenderer.send(ACTION_RUN_APK_DEPENDENCY_GRAPH, config)

            // After sending an event to the Main process, it is necessary
            // to listen for the load failure event for updating the UI
            // when happening one.
            const onApkAnalysisFailListener = (event, error) => {
                ipcRenderer.removeAllListeners(ACTION_FAIL_APK_DEPENDENCY_GRAPH)
                reject(error)
            }
            ipcRenderer.on(
                ACTION_FAIL_APK_DEPENDENCY_GRAPH, onApkAnalysisFailListener)
        })
    }
}

module.exports = {
    ApkDependencyController: ApkDependencyController,
    ApkDependencyAnalyzer:  ApkDependencyAnalyzer
}