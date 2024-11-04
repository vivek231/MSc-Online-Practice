import qupath.lib.regions.RegionRequest
import qupath.lib.images.writers.ImageWriterTools

// Define output directory
def baseOutputDir = "G:/Cancer & Inflammation/VSM/Oscar/CLAUDE_FIELD CANCERIZATION/TCGA/Annotations"

// Define patch size
int patchSize = 256

// Get the server for the current image
def server = getCurrentImageData().getServer()

// Iterate over all annotations in the image
for (annotation in getAnnotationObjects()) {
    // Get annotation label
    def label = annotation.getPathClass().toString()
    
    // Get image name (without extension)
    def imageName = getCurrentImageData().getServer().getMetadata().getName().split("\\.")[0]

    // Define paths for the image folder and label subfolder
    def imageFolder = new File(baseOutputDir, imageName)
    def labelFolder = new File(imageFolder, label)
    if (!labelFolder.exists()) {
        labelFolder.mkdirs()
    }

    // Define bounds for the annotation
    def roi = annotation.getROI()
    def minX = roi.getBoundsX()
    def minY = roi.getBoundsY()
    def width = roi.getBoundsWidth()
    def height = roi.getBoundsHeight()

    // Loop over rows and columns to export tiles
    for (int x = minX; x < minX + width; x += patchSize) {
        for (int y = minY; y < minY + height; y += patchSize) {
            def request = RegionRequest.createInstance(server.getPath(), 1, x, y, patchSize, patchSize)
            def exportPath = new File(labelFolder, "${label}_${x}_${y}_${patchSize}px.tif")
            ImageWriterTools.writeImageRegion(server, request, exportPath.getAbsolutePath())
        }
    }
}

print("Export complete. Check folders at: " + baseOutputDir)
