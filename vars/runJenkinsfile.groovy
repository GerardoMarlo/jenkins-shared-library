
def call() {
    // Securely detect the actual user who triggered the build
    def user = "unknown"
    
    try {
        def causes = currentBuild.getBuildCauses()
        if (causes) {
            user = causes[0]?.userId ?: "anonymous"
        }
    } catch (Exception e) {
        echo "Failed to get user info: ${e.message}"
    }

    echo "Detected user: ${user}"

    // Define which Jenkinsfile to execute based on the user
    def jenkinsfilePath = "Jenkinsfile.default"

    if (user == "vendor") {
        jenkinsfilePath = "Jenkinsfile.vendor"
    }

    echo "Executing Jenkinsfile: ${jenkinsfilePath}"
    // Read and evaluate the Jenkinsfile from the shared library
    def jenkinsfileContent  = libraryResource(jenkinsfilePath)
    def jenkinsfilePath = "${env.WORKSPACE}/${jenkinsfileName}"
    writeFile file: jenkinsfilePath, text: jenkinsfileContent
    try {
        // Load and execute the Jenkinsfile
        load jenkinsfilePath
    } finally {
        // Ensure the file is deleted even if there's an error
        echo "Deleting Jenkinsfile: ${jenkinsfilePath}"
        sh "rm -f ${jenkinsfilePath}"
    }
}