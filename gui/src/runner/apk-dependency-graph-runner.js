
const fs = require('fs')
const path = require('path')
const { exec } = require('child_process')

const hasApkDependencyGraphJar = (currentDir) => 
    fs.existsSync(getApkDependencyGraphJarPath(currentDir))

const getApkDependencyGraphJarPath = (currentDir) =>
    path.join(currentDir, "lib/apk-dependency-graph.jar")

const makeDependencyGraphCommand = (currentDir, fileName, filter, isInner) => {
    const extension = path.extname(fileName)
    const xpref = path.basename(fileName, extension)

    const jsonPath = `${currentDir}/analyzed.js`
    const outPath = `${currentDir}/output/${xpref}`
    const libPath = getApkDependencyGraphJarPath(currentDir)

    return `java -jar ${libPath} -i ${outPath} -a ${fileName} -o ${jsonPath} -f ${filter} -d ${isInner}`
}

class ApkDependencyGraphRunner {
    constructor(currentDir) {
        this.currentDir = currentDir
    }

    run(config) {
        return new Promise((resolve, reject) => {
            if (!hasApkDependencyGraphJar(this.currentDir)) {
                reject("Without apk-dependency-graph jar!")
                return
            }

            if (!config) {
                reject("Without info to run!")
            }

            if (config.apkFile === undefined) {
                reject("Without an APK file!")
                return
            }

            const command = makeDependencyGraphCommand(
                this.currentDir,
                config.apkFile,
                config.filter != undefined ? config.filter : "",
                config.isInnerEnabled)

            if (command == undefined || command === "") {
                reject("Without command to run!")
                return
            }

            exec(command, (error, stdout, stderr) => {
                if (error) return reject(error)
                if (stderr) return reject(stderr)
                resolve()
            })
        })
    }
}

module.exports = ApkDependencyGraphRunner