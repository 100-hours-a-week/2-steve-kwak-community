document.addEventListener("DOMContentLoaded", async () => {
    const postId = document.body.getAttribute('data-post-id');
    const userId = localStorage.getItem("user_id");

    if (!postId) {
        alert("잘못된 접근입니다.");
        window.location.href = "/posts";
        return;
    }
    if (!userId) {
        alert("로그인이 필요합니다.");
        window.location.href = "/users/login";
        return;
    }

    try {
        const response = await fetch(`/posts/${postId}`);
        if (!response.ok) throw new Error("게시글을 불러오지 못했습니다.");

        const post = await response.json();
        document.querySelector(".post-title").textContent = post.title;
        document.querySelector(".post-author").textContent = post.author;  // author를 수정된 방식으로 표시
        document.querySelector(".post-date").textContent = post.createdAt;
        document.querySelector(".post-content").textContent = post.content;
        document.querySelector(".like-btn").innerHTML = `👍 ${post.likeCount}`;
        document.querySelector(".view-count").textContent = `조회수 ${post.viewCount}`;
        document.querySelector(".comment-count").textContent = `댓글 ${post.commentCount}`;

        // 댓글 데이터 불러오기
        const commentResponse = await fetch(`/posts/${postId}/comments`);
        if (!commentResponse.ok) throw new Error("댓글을 불러오지 못했습니다.");

        const comments = await commentResponse.json();
        renderComments(comments); // 댓글 렌더링
    } catch (error) {
        console.error(error);
        alert("게시글을 불러오는 중 오류가 발생했습니다.");
    }
});

// 게시글 수정 버튼 클릭
document.querySelector(".edit-btn").addEventListener("click", () => {
    const postId = document.body.getAttribute('data-post-id');
    window.location.href = `/posts/postedit/${postId}`;
});

// 좋아요 버튼 클릭 이벤트
document.querySelector(".like-btn").addEventListener("click", async () => {
    const postId = document.body.getAttribute('data-post-id');
    const userId = localStorage.getItem("user_id");

    try {
        // userId와 postId를 JSON 데이터로 전달
        const response = await fetch(`/posts/${postId}/like`, {
            method: "PATCH",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ userId: userId, postId: postId }) // 데이터로 보내기
        });

        if (!response.ok) throw new Error("좋아요 요청 실패");

        const updatedPost = await response.json();  // 서버에서 최신 게시글 정보 (좋아요 갯수 포함) 받기
        document.querySelector(".like-btn").innerHTML = `👍 ${updatedPost.likeCount}`;  // 최신 좋아요 수 업데이트
    } catch (error) {
        console.error(error);
        alert("이미 좋아요를 누른 게시물입니다.");
    }
});

// 게시글 삭제
document.querySelector(".delete-btn").addEventListener("click", async () => {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    const postId = document.body.getAttribute('data-post-id');

    try {
        const response = await fetch(`/posts/${postId}`, { method: "DELETE" });
        if (!response.ok) throw new Error("삭제 실패");

        alert("게시글이 삭제되었습니다.");
        window.location.href = "/posts";
    } catch (error) {
        console.error(error);
        alert("게시글 삭제에 실패했습니다.");
    }
});

// 댓글 추가 기능
async function addComment() {
    const postId = document.body.getAttribute('data-post-id');
    const userId = localStorage.getItem("user_id");
    const commentInput = document.querySelector(".comment-input");
    const commentContent = commentInput.value.trim();

    if (!commentContent) {
        alert("댓글을 입력하세요.");
        return;
    }

    try {
        const response = await fetch(`/posts/${postId}/comments`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                postId: postId,
                userId: userId,
                content: commentContent,
            }),
        });

        if (!response.ok) throw new Error("댓글 등록 실패");

        const newComment = await response.json();
        commentInput.value = ""; // 입력 필드 초기화
        window.location.href = `/posts/api/${postId}`;
    } catch (error) {
        console.error(error);
        alert("댓글 등록에 실패했습니다.");
    }
}

// 댓글 리스트 렌더링
function renderComments(comments) {
    const commentList = document.querySelector(".comment-list");
    commentList.innerHTML = "";

    if (comments.length === 0) {
        commentList.innerHTML = "<p>댓글이 없습니다.</p>";
        return;
    }

    comments.forEach(renderNewComment); // 각 댓글에 대해 renderNewComment 호출
}

// 새로운 댓글을 화면에 추가하는 함수
function renderNewComment(comment) {
    const commentList = document.querySelector(".comment-list");

    const commentItem = document.createElement("div");
    commentItem.classList.add("comment-item");
    commentItem.innerHTML = `
        <div class="comment-header">
            <span class="comment-author">${comment.nickname}</span>
            <span class="comment-date">${new Date(comment.createdAt).toLocaleString()}</span>
        </div>
        <p class="comment-content">${comment.content}</p>
    `;

    commentList.appendChild(commentItem);
}

// 댓글 등록 버튼 클릭 이벤트 추가
document.querySelector(".comment-submit").addEventListener("click", addComment);
