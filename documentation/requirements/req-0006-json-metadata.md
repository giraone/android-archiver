# REQ-0006 JSON Metadata

Change the software, so that for each stored file a set of key value pairs can be additionally stored in a JSON file. This key value pairs will be called "metadata" from now on. One always present key value pair will be the key "name" and the file name. Another one will be "contentType" and the content type of the file, e.g. "image/jpeg". So from now on, the file name in the file system will be only "<tsid>.content", where <tsid> is the unique identifier of the file. So, there is no need for mapping content type to the file extension anymore.

## Result:

● Perfect! I have successfully implemented the metadata functionality for the Android archiver application. Here's a summary of the changes made:

Implementation Summary

1. New File Structure

- Files are now stored as {TSID}.content (just the content without original filename/extension)
- Metadata is stored in separate {TSID}.metadata JSON files
- The JSON metadata always contains name (original filename) and contentType (MIME type)

2. Core Components Modified

FileItem.kt: Added metadata: Map<String, String> field

MetadataUtils.kt: New utility class for:
- Creating default metadata with name and contentType
- Reading/writing JSON metadata files
- Extracting TSID from .content and .metadata filenames

FileUtils.kt: Updated generateUniqueFileName() to return {TSID}.content format

FileRepository.kt: Completely updated to:
- Load files by scanning for .content files and reading corresponding .metadata files
- Create metadata files when adding new files
- Delete both content and metadata files when deleting
- Update metadata files when renaming (no file system rename needed)

ContentHandler.kt: Updated to work with new file naming convention

3. File Type Detection

Content type detection now uses metadata instead of file extensions, eliminating the need to map content types to file extensions.

4. Tests Updated

- FileUtilsTest.kt: Updated tests for new filename format
- MetadataUtilsTest.kt: New test file for metadata functionality

5. Key Benefits Achieved

✅ Separation of concerns: File content and metadata are stored separately
✅ Flexible metadata: Can store any key-value pairs in addition to name and contentType
✅ No extension dependency: Content type is stored in metadata, not inferred from filename
✅ Cleaner file storage: All content files have uniform .content extension
✅ Backward compatibility: Old files can be migrated by creating corresponding metadata files

The implementation is complete and ready for testing.