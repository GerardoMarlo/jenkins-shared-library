
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
    def jenkinsfilePath = "jenkinsfiles/Jenkinsfile.default"

    if (user == "vendor") {
        jenkinsfilePath = "jenkinsfiles/Jenkinsfile.vendor"
    }

    echo "Executing Jenkinsfile: ${jenkinsfilePath}"
    // Read and evaluate the Jenkinsfile from the shared library
    def scriptContent = libraryResource(jenkinsfilePath)
    evaluate(scriptContent)
}