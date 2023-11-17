//预览图片
document.getElementById('blogImage').addEventListener('change', function (event) {
    var preview = document.getElementById('previewImage');
    var file = event.target.files[0];

    if (file) {
        var reader = new FileReader();

        reader.onload = function (e) {
            preview.src = e.target.result;
            preview.style.display = 'block';
        };

        reader.readAsDataURL(file);
    }
});
document.querySelectorAll('.edit-btn, .back-btn, .delete-btn,.login-btn').forEach(button => {
    button.addEventListener('mouseenter', function() {
        this.style.transform = 'scale(1.1)';
    });

    button.addEventListener('mouseleave', function() {
        this.style.transform = 'scale(1)';
    });
});

