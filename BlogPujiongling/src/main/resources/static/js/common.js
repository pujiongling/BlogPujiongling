// 图片预览功能
document.getElementById('blogImage').addEventListener('change', function(event) {
	// 获取预览图片元素
	var preview = document.getElementById('previewImage');
	// 获取选择的文件
	var file = event.target.files[0];

	// 检查是否选择了文件
	if (file) {
		// 创建一个FileReader对象
		var reader = new FileReader();

		// 读取文件完成后的回调函数
		reader.onload = function(e) {
			// 将文件读取的结果设置为预览图片的源
			preview.src = e.target.result;
			// 显示预览图片
			preview.style.display = 'block';
		};

		// 读取文件内容并触发回调函数
		reader.readAsDataURL(file);
	} else {
		// 如果未选择文件，隐藏预览图片
		preview.style.display = 'none';
	}
});

// 文件选择后触发的事件处理函数
function handleFileSelect() {
	var registeredImage = document.getElementById('registeredImage');
	var previewImage = document.getElementById('previewImage');
	var blogImageInput = document.getElementById('blogImage');

	// 检查是否有新文件上传
	if (blogImageInput.files && blogImageInput.files[0]) {
		// 有新文件上传，隐藏registeredImage，显示新上传的图片预览
		registeredImage.style.display = 'none';
		previewImage.style.display = 'block';

		// 设置新上传图片的预览
		var reader = new FileReader();
		reader.onload = function(e) {
			previewImage.src = e.target.result;
		};
		reader.readAsDataURL(blogImageInput.files[0]);
	} else {
		// 没有新文件上传，显示registeredImage，隐藏预览
		registeredImage.style.display = 'block';
		previewImage.style.display = 'none';
	}
}


// 给指定的按钮添加鼠标进入和离开事件监听器
document.querySelectorAll('.edit-btn, .back-btn, .delete-btn, .login-btn').forEach(button => {
	button.addEventListener('mouseenter', function() {
		this.style.transform = 'scale(1.1)';
	});

	button.addEventListener('mouseleave', function() {
		this.style.transform = 'scale(1)';
	});
});
