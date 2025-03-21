document.addEventListener("DOMContentLoaded", async function () {
    const titleInput = document.getElementById("title");
    const contentInput = document.getElementById("content");
    const imageInput = document.getElementById("image");
    const editForm = document.querySelector(".edit-form");

    // 현재 페이지 URL에서 postId 가져오기
    const postId = document.body.getAttribute('data-post-id');
    const token = localStorage.getItem("token");

    if (!postId) {
        alert("잘못된 접근입니다.");
        window.location.href = "/posts";
        return;
    }

    if (!token) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
        return;
    }

    // 서버로 요청하기 전에 Authorization 헤더 확인
    console.log("Authorization Header: Bearer " + token); // 헤더 확인

    try {
        const response = await fetch(`/posts/${postId}`, {
            headers: {
                "Authorization": `Bearer ${token}` // JWT 토큰을 Authorization 헤더에 포함
            }
        });

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

    editForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const updatedPost = {
            title: titleInput.value,
            content: contentInput.value
        };

        const headers = {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        };
        console.log('Request Headers:', headers);
        console.log('Request Body:', JSON.stringify(updatedPost));
        try {
            const response = await fetch(`/posts/${postId}`, {
                method: "PUT",
                headers: headers,
                body: JSON.stringify(updatedPost)
            });
            console.log(response)

            if (!response.ok) {
                const errorText = await response.text();  // 에러 응답 내용 받기
                console.error("Error response:", errorText); // 서버에서 반환한 에러 내용
                throw new Error("게시글 수정 실패");
            }

            alert("게시글이 수정되었습니다.");
            window.location.href = `/posts/api1/${postId}`;
        } catch (error) {
            console.error("Error occurred:", error);  // 에러 상세 출력
            alert("게시글 수정에 실패했습니다.");
        }
    });

});
