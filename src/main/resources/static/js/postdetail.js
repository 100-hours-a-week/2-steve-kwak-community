document.addEventListener("DOMContentLoaded", async () => {
    // body에서 data-post-id를 가져와 postId로 사용
    const postId = document.body.getAttribute('data-post-id');
    if (!postId) {
        alert("잘못된 접근입니다.");
        window.location.href = "/posts";
        return;
    }
    const userId = localStorage.getItem("user_id");
    if (!userId) {
        alert("로그인이 필요합니다.");
        window.location.href = "/users/login"; // 로그인 페이지로 이동
        return;
    }

    try {
        const response = await fetch(`/posts/${postId}`);  // 경로 수정
        if (!response.ok) throw new Error("게시글을 불러오지 못했습니다.");

        const post = await response.json();
        document.querySelector(".post-title").textContent = post.title;
        document.querySelector(".post-author").textContent = post.author;
        document.querySelector(".post-date").textContent = post.createdAt;
        document.querySelector(".post-content").textContent = post.content;
        document.querySelector(".like-btn").innerHTML = `👍 ${post.likeCount}`;
        document.querySelector(".view-count").textContent = `조회수 ${post.viewCount}`;
        document.querySelector(".comment-count").textContent = `댓글 ${post.commentCount}`;

        renderComments(post.comments);
    } catch (error) {
        console.error(error);
        alert("게시글을 불러오는 중 오류가 발생했습니다.");
    }
});

// 게시글 수정 버튼 클릭
document.querySelector(".edit-btn").addEventListener("click", () => {
    const postId = document.body.getAttribute('data-post-id');  // 수정된 부분
    window.location.href = `/posts/postedit/${postId}`;
});

// 좋아요 버튼 클릭 이벤트
document.querySelector(".like-btn").addEventListener("click", async () => {
    const postId = document.body.getAttribute('data-post-id');  // 수정된 부분

    try {
        const response = await fetch(`/api/posts/${postId}/like`, { method: "PATCH" });  // /api/posts/{postId}/like 경로 수정
        if (!response.ok) throw new Error("좋아요 요청 실패");
        const updatedPost = await response.json();
        document.querySelector(".like-btn").innerHTML = `👍 ${updatedPost.likeCount}`;
    } catch (error) {
        console.error(error);
        alert("좋아요를 반영하지 못했습니다.");
    }
});

// 게시글 삭제
document.querySelector(".delete-btn").addEventListener("click", async () => {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    const postId = document.body.getAttribute('data-post-id');  // 수정된 부분

    try {
        const response = await fetch(`/posts/${postId}`, { method: "DELETE" });  // /api/posts/{postId} 경로 수정
        if (!response.ok) throw new Error("삭제 실패");

        alert("게시글이 삭제되었습니다.");
        window.location.href = "/posts";
    } catch (error) {
        console.error(error);
        alert("게시글 삭제에 실패했습니다.");
    }
});

// 댓글 렌더링
function renderComments(comments) {
    const commentList = document.querySelector(".comment-list");
    commentList.innerHTML = "";

    if (comments.length === 0) {
        commentList.innerHTML = "<p>댓글이 없습니다.</p>";
        return;
    }

    comments.forEach(comment => {
        const commentItem = document.createElement("div");
        commentItem.classList.add("comment-item");
        commentItem.innerHTML = `
            <p class="comment-author">${comment.author}</p>
            <p class="comment-date">${new Date(comment.createdAt).toLocaleString()}</p>
            <p class="comment-content">${comment.content}</p>
        `;
        commentList.appendChild(commentItem);
    });
}x``
