
const { ApkDependencyAnalyzer } = require('./apk-dependency-analyzer-controller')
const FileExplorerDialog = require('./file-explorer-dialog')

class HomePage {
    constructor() {
        this.isPreviewProcessing = false
        this.selectedFile = undefined
        this.fileExplorer = undefined
    }

    onload() {
        this.fileExplorer = new FileExplorerDialog()

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
    }

    selectCurrentFilePath(filePath) {
        this.selectedFile = filePath
        document.getElementById('drag-title').textContent = filePath
    }

    isInnerClass() {
        let checkbox =  document.getElementById('checkbox-inner-class')
        return checkbox.checked ? true : false
    }

    getFilterValue() {
        let inputFilter =  document.getElementById('input-filter')
        return inputFilter !== undefined ? inputFilter.value : ""
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
