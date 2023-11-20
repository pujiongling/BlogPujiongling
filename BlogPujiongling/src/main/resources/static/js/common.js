// 图片预览功能
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
// 给指定的按钮添加鼠标进入和离开事件监听器
document.querySelectorAll('.edit-btn, .back-btn, .delete-btn, .login-btn').forEach(button => {
    button.addEventListener('mouseenter', function () {
        this.style.transform = 'scale(1.1)';
    });

    button.addEventListener('mouseleave', function () {
        this.style.transform = 'scale(1)';
    });
});
