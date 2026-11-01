
const status = document.getElementById("uploadStatus");
const form = document.getElementById("uploadForm");
form.addEventListener("submit",function (event) {

    event.preventDefault();

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
