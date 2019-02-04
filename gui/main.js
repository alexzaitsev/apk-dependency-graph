
let { app } = require('electron')

const HomeBrowserWindow = require('./src/home/home-browser-window')
const ViewerBrowserWindow = require('./src/viewer/viewer-browser-window')
const EventBus = require('./src/util/event-bus')

let homeWindow = undefined
const appConfig = {
    title: "APK Dependency Graph",
    devMode: true
}

const ACTION_OPEN_VIEWER_PAGE = 'open-viewer-page'
const ACTION_OPEN_HOME_PAGE = 'open-home-page'

EventBus.on(ACTION_OPEN_HOME_PAGE, () => {
    openHomePage()
})

EventBus.on(ACTION_OPEN_VIEWER_PAGE, () => {
    openViewerPage()
    closeHomePage()
})

app.on('ready', openHomePage)
app.on('activate', openHomePage)
app.on('window-all-closed', closeAllWindows)

function isAvaliable(window) {
    return window !== undefined && window !== null
}

function openHomePage() {
    if (!isAvaliable(homeWindow)) {
        console.log("Opening home page")
        homeWindow = new HomeBrowserWindow(app, appConfig)
        homeWindow.open()
    }
}

function closeHomePage() {
    if (isAvaliable(homeWindow)) {
        console.log("closing home page")
        homeWindow.close()
        homeWindow = undefined
    }
}

function openViewerPage() {
    console.log("Opening viewer page")
    let viewerPage = new ViewerBrowserWindow(appConfig)
    viewerPage.open()
}

function closeAllWindows() {
    if (process.platform !== 'darwin') {
        app.quit()
    }
}
