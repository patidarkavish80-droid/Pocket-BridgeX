# Pocket-Bridge

Pocket-Bridge is a Java-based file transfer application that allows users to transfer files from their phone to their laptop through a web browser using the same local network.

## Features

- Automatically detects and displays the laptop's local IP address.
- Opens a web page on the phone through a browser.
- Uploads files from phone to laptop.
- Saves uploaded files in the `uploads` folder.
- Uses a custom Java HTTP server.
- Handles multiple client connections sequentially.
- Handles client disconnections without stopping the server.

## Technologies Used

- Java
- HTML
- CSS
- Java Networking (`ServerSocket`, `Socket`)
- HTTP
- Git and GitHub

## How to Run

1. Run the Java server from your IDE.
2. Make sure your phone and laptop are connected to the same Wi-Fi network.
3. Check the IP address displayed in the terminal.
4. Open the displayed URL in your phone's browser.
5. Select a file and upload it.
6. Find the uploaded file inside the `uploads` folder.

## Project Structure

```text
Pocket-Bridge/
├── src/
│   └── main/
│       ├── java/
│       │   └── Main.java
│       └── resources/
│           ├── index.html
│           └── style.css
├── uploads/
├── .gitignore
└── README.md
```

## Current Status

The project is under development. More features and improvements will be added in future updates.

## Future Improvements

- Better upload error handling.
- Support for larger files.
- Improved user interface.
- Secure file transfer.
- Internet-based file transfer.

## Author

Kavish Patidar