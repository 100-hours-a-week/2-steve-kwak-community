document.addEventListener("DOMContentLoaded", async function () {
    const titleInput = document.getElementById("title");
    const contentInput = document.getElementById("content");
    const imageInput = document.getElementById("image");
    const editForm = document.querySelector(".edit-form");

    // 현재 페이지 URL에서 postId 가져오기
    const postId = document.body.getAttribute('data-post-id');
    if (!postId) {
        alert("잘못된 접근입니다.");
        window.location.href = "/posts";
        return;
    }


    try {
        const response = await fetch(`/posts/${postId}`);
        if (!response.ok) throw new Error("게시글을 불러오지 못했습니다.");

        const post = await response.json();
        titleInput.value = post.title;
        contentInput.value = post.content;
        // 현재 이미지가 있으면 설정
        if (post.imageUrl) {
            const currentImage = document.getElementById("current-image");
            currentImage.src = post.imageUrl;
            currentImage.style.display = "block";
        }
    } catch (error) {
        console.error(error);
        alert("게시글을 불러오는 중 오류가 발생했습니다.");
    }

    // 게시글 수정 요청
    editForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const updatedPost = {
            title: titleInput.value,
            content: contentInput.value,
            imageUrl: imageInput.value || ""  // 이미지가 변경되지 않았다면 기존 값 유지
        };

        try {
            const response = await fetch(`/posts/${postId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(updatedPost)
            });

            if (!response.ok) throw new Error("게시글 수정 실패");

            alert("게시글이 수정되었습니다.");
            window.location.href = `/posts/api/${postId}`;
        } catch (error) {
            console.error(error);
            alert("게시글 수정에 실패했습니다.");
        }
    });
});
