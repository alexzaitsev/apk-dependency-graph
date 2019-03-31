
const electron = require('electron')
const path = require('path')
const fs = require('fs')

const readFileAsJson = (filePath, defaults) => new Promise((resolve, reject) => {
    fs.readFile(filePath, (error, data) => {
        if (error) {
            resolve(defaults)
        } else {
            try {
                resolve(JSON.parse(data))
            } catch(exception) {
                console.log(exception)
                resolve(defaults)
            }
        }
    })
})
  
class Store {
    constructor(opts) {
        const userDataPath = (electron.app || electron.remote.app).getPath('userData')
        this.path = path.join(userDataPath, opts.configName + '.json')
        this.data = {}
        this.defaults = opts.defaults
    }

    load(callback) {
        readFileAsJson(this.path, this.defaults)
            .then((data) => {
                this.data = data
                callback()
            })
    }

    get(key) {
        return this.data[key]
    }

    set(key, val) {
        this.data[key] = val
        fs.writeFile(this.path, JSON.stringify(this.data), (err) => {
            if (!err) console.log("save")
        })
    }
}

module.exports = Store
