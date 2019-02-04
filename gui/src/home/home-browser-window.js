
const { BrowserWindow } = require('electron')
const { ApkDependencyController } = require('./apk-dependency-analyzer-controller')

const HOME_WINDOW_WIDTH  = 360
const HOME_WINDOW_HEIGHT = 380

class HomeBrowserWindow {
    constructor(app, info) {
        this.devMode = info.devMode != undefined ? info.devMode : false
        this.title = info.title != undefined ? info.title : ""
        this.window = null
        this.apkDependencyController =
                new ApkDependencyController(app.getAppPath())
    }

    open() {
        // Create the browser window.
        this.window = new BrowserWindow({
            width: HOME_WINDOW_WIDTH,
            height: HOME_WINDOW_HEIGHT,
            title: this.title,
            resizable: false,
            useContentSize: true,
            nodeIntegration: false
        })

        if (!this.devMode) {
            this.window.setMenu(null)
        }

        this.window.loadFile('src/home/home.html')
        this.window.on('closed', this.onWindowClosed.bind(this))

        this.apkDependencyController.start();
    }

    close() {
        this.window.close()
        this.apkDependencyController.stop();
    }

    onWindowClosed() {
        this.window = null
    }
}

module.exports = HomeBrowserWindow
