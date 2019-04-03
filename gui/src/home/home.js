
const { ApkDependencyAnalyzer } = require('./apk-dependency-analyzer-controller')
const FileExplorerDialog = require('./file-explorer-dialog')
const Store = require('./../util/store')
const fs = require('fs')

class HomePage {
    constructor() {
        this.isPreviewProcessing = false
        this.selectedFile = undefined
        this.fileExplorer = undefined
        this.userPreference = undefined
    }

    onload() {
        this.fileExplorer = new FileExplorerDialog()
        this.userPreference = new Store({configName: 'user-preference', defaults: {}})

        let buttonPreview =  document.getElementById('btn-preview')
        buttonPreview.onclick = this.onClickPreview.bind(this);
        
        let holder =  document.getElementById('drag-file')
        holder.ondragover = () => false
        holder.ondragleave = () => false
        holder.ondragend = () => false
        holder.ondrop = (e) => {
            e.preventDefault()
            let file = e.dataTransfer.files[0]
            if (file != undefined) {
                this.selectCurrentFilePath(file.path)
            }
            return false
        }

        holder.onclick = () => {
            this.fileExplorer.show()
                .then((result) => {
                    this.selectCurrentFilePath(result)
                })
        }

        this.innerClassElement = document.getElementById('checkbox-inner-class')
        this.inputFilterElement = document.getElementById('input-filter')
        this.dragTitleElement = document.getElementById('drag-title')

        this.userPreference.load(() => {
            this.restorePreviousValues()
        })

        let self = this;
        this.innerClassElement.onchange = function() {
            self.userPreference.set("inner-class", this.checked)
        }
        this.inputFilterElement.onchange = () => {
            this.userPreference.set("input-filter", this.inputFilterElement.value)
        }
    }

    restorePreviousValues() {
        let apkFile = this.userPreference.get("apk-file")
        fs.exists(apkFile, (exists) => {
            if (exists && apkFile !== undefined) {
                this.dragTitleElement.textContent = apkFile
                this.selectedFile = apkFile
            }
        });

        let innerClass = this.userPreference.get("inner-class");
        if (innerClass !== undefined) {
            this.innerClassElement.checked = innerClass
        }

        let inputFilter = this.userPreference.get("input-filter")
        if (inputFilter !== undefined) {
            this.inputFilterElement.value = inputFilter
        }
    }

    selectCurrentFilePath(filePath) {
        this.selectedFile = filePath
        this.dragTitleElement.textContent = filePath
        this.userPreference.set("apk-file", filePath)
    }

    isInnerClass() {
        return this.innerClassElement !== undefined && this.innerClassElement.checked
    }

    getFilterValue() {
        return this.inputFilterElement !== undefined ? this.inputFilterElement.value : ""
    }

    onClickPreview() {
        if (this.selectedFile == undefined) {
            alert("Without a selected apk file!")
            return
        }

        this.showPreviewLoading()

        this.loadApkDependencyGraph(
            this.selectedFile,
            this.getFilterValue(),
            this.isInnerClass())
    }

    loadApkDependencyGraph(apkFile, filter, inner) {
        if (this.isPreviewProcessing) return
        this.isPreviewProcessing = true

        new ApkDependencyAnalyzer().analyze({
            apkFile: apkFile,
            filter: filter,
            isInnerEnabled: inner
        })
            // TODO: For now, It is not necessary to implement the success
            // case when finishing the APK analysis process, because the
            // viewer window is called so after completing the analysis.
            // .then(/success case/)
            .catch(this.onFailPreviewLoader.bind(this));
    }

    onFailPreviewLoader(error) {
        this.isPreviewProcessing = false
        this.hiddenPreviewLoading()
        alert(this.getErrorMessage(error))
    }

    showPreviewLoading() {
        let loading =  document.getElementById("preview-loading")
        if (loading) loading.style.visibility = "visible"
    }

    hiddenPreviewLoading() {
        let loading =  document.getElementById("preview-loading")
        if (loading) loading.style.visibility = "hidden"
    }

    getErrorMessage(error) {
        if (typeof error === "string") {
            return error
        } else if (error.message !== undefined) {
            return error.message
        } else {
            return "Happen System Error: " + error
        }
    }
}

(function() {
    let homePage = new HomePage();

    window.onload = function() {
        homePage.onload();
    }
})()
