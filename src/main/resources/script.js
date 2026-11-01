
const status = document.getElementById("uploadStatus");
const form = document.getElementById("uploadForm");
form.addEventListener("submit",function (event) {

    event.preventDefault();
    const fileInput = document.getElementById("file");
    const file = fileInput.files[0];

    const maxSize = 500 * 1024 * 1024;

    if (file.size > maxSize) {
        status.textContent = "File is too large! Maximum size is 500 MB.";
        return;
    }

    status.textContent = "Uploading...";

    const formData = new FormData(form);

    fetch("/upload", {
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(result => {
            status.textContent = result;
        })
        .catch(error => {
            status.textContent = "Upload failed!";
            console.log(error);
        });
});
