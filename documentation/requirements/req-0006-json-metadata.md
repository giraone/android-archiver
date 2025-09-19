# REQ-0006 JSON Metadata

Change the software, so that for each stored file a set of key value pairs can be additionally stored in a JSON file. This key value pairs will be called "metadata" from now on. One always present key value pair will be the key "name" and the file name. Another one will be "contentType" and the content type of the file, e.g. "image/jpeg". So from now on, the file name in the file system will be only "<tsid>.content", where <tsid> is the unique identifier of the file. So, there is no need for mapping content type to the file extension anymore.

## Result: