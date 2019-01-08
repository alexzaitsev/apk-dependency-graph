
const { BrowserWindow } = require('electron')

class ViewerBrowserWindow {
    constructor(obj) {
        this.devMode = obj.devMode != undefined ? obj.devMode : false
        this.appTitle = obj.title != undefined ? obj.title : ""
        this.window = null
    }

    open() {
        this.window = new BrowserWindow(setFullScreen({
            title: this.appTitle,
            nodeIntegration: false
        }))

        if (!this.devMode) {
            this.window.setMenu(null)
        }

        this.window.loadFile("src/viewer/viewer.html")
        this.window.on('closed', this.onWindowClosed.bind(this))
    }

    close() {
        this.window.close()
    }

    onWindowClosed() {
        this.window = null
    }
}

function setFullScreen (windowInfo) {
    const { screen } = require('electron')
    let mainScreen = screen.getPrimaryDisplay()
    let dimensions = mainScreen.size
    windowInfo.width = dimensions.width
    windowInfo.height = dimensions.height
    return windowInfo
}

module.exports = ViewerBrowserWindow
