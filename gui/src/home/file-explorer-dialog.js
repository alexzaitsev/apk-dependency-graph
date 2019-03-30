
const { dialog } = require('electron').remote

function openFileExplorerDialog(callback) {
    const options = { 
        filters: [
            { name: 'APK', extensions: ['apk'] },
            { name: 'All Files', extensions: ['*'] }
        ],
        title: "Select an APK file",
        properties: ['openFile']
    }
    dialog.showOpenDialog(options, (filePaths) => {
        callback(filePaths)
    })
}

class FileExplorerDialog {
    show() {
        return new Promise((resolve, reject) => {
            openFileExplorerDialog((filePaths) => {
                if (filePaths != undefined && filePaths.length > 0) {
                    resolve(filePaths[0])
                }
            })
        })
    }
}

module.exports = FileExplorerDialog
