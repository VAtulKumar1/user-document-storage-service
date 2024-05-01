# user-document-storage-service


## Introduction
This repository contains a backend REST APIs for a user document storage service. The API allows users to search and download files stored in an Amazon S3 bucket. Each user has their own folder within the S3 bucket, ensuring that users can only access their files. The project is designed with extensibility in mind and follows REST compliance.

## Features
- **Search Files**: Allows users to search for files in their respective S3 folders based on file name.
- **Upload Files (Optional)**: Provides an endpoint to upload files to the S3 bucket. This feature is optional.
- **File Download**: Once the desired file is found, users can download it.


## APIs
The following REST APIs are provided:

### Search Files
- **Method**: GET
- **Endpoint**: `/api/files/search`
- **Parameters**:
  - `userName`: The name of the user (e.g., "sandy").
  - `fileName`: A term to search for in file names (e.g., "logistics").
- **Response**: A list of files matching the search term within the specified user's folder.

### Upload Files (Optional)
- **Method**: POST
- **Endpoint**: `/api/files/upload`
- **Parameters**:
  - `userName`: The name of the user (e.g., "sandy").
- **File**: The file to be uploaded (multipart/form-data).
- **Response**: A success or error message indicating the outcome of the upload operation.

### Download Files
- **Method**: GET
- **Endpoint**: `/api/files/download`
- **Parameters**:
  - `userName`: The name of the user (e.g., "sandy").
  - `fileName`: The name of the file to be downloaded.
- **Response**: The requested file as a downloadable attachment.

## Integration with S3
- The S3 bucket is used to store all files.
- Each user has their own folder within the S3 bucket.
- The API interacts with S3 to search, upload, and download files.

