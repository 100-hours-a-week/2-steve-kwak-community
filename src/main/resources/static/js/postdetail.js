document.addEventListener("DOMContentLoaded", async () => {
    const postId = document.body.getAttribute('data-post-id');
    const token = localStorage.getItem("token");

    if (!postId) {
        alert("잘못된 접근입니다.");
        window.location.href = "/posts";
        return;
    }
    if (!token) {
        alert("로그인이 필요합니다1.");
        window.location.href = "/users/login";
        return;
    }

    try {
        const response = await fetch(`/posts/${postId}`, {
            headers: { Authorization: `Bearer ${token}` } // JWT 인증 추가
        });

        if (response.status === 401) {
            alert("로그인이 필요합니다2.");
            window.location.href = "/users/login";
            return;
        }
        if (!response.ok) throw new Error("게시글을 불러오지 못했습니다.");

        const post = await response.json();
        document.querySelector(".post-title").textContent = post.title;
        document.querySelector(".post-author").textContent = post.author;
        document.querySelector(".post-date").textContent = post.createdAt;
        document.querySelector(".post-content").textContent = post.content;
        document.querySelector(".like-btn").innerHTML = `👍 ${post.likeCount}`;
        document.querySelector(".view-count").textContent = `조회수 ${post.viewCount}`;
        document.querySelector(".comment-count").textContent = `댓글 ${post.commentCount}`;

        // 이미지 URL이 있으면 이미지 태그에 추가
        if (post.imageUrl) {
            const imageElement = document.querySelector(".post-image");
            imageElement.src = post.imageUrl;
            imageElement.alt = "게시글 이미지"; // 이미지 설명 추가
            imageElement.style.display = "block"; // 이미지 보이게 설정
        }

        // 댓글 불러오기
        const commentResponse = await fetch(`/posts/${postId}/comments`, {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (commentResponse.status === 401) {
            alert("로그인이 필요합니다.");
            window.location.href = "/users/login";
            return;
        }
        if (!commentResponse.ok) throw new Error("댓글을 불러오지 못했습니다.");

        const comments = await commentResponse.json();
        renderComments(comments);
    } catch (error) {
        console.error(error);
        alert("게시글을 불러오는 중 오류가 발생했습니다.");
    }
});


// 게시글 수정 버튼 클릭
document.querySelector(".edit-btn").addEventListener("click", () => {
    const postId = document.body.getAttribute('data-post-id');
    const token = localStorage.getItem("token");
    window.location.href = `/posts/postedit/${postId}`;
});

// 좋아요 버튼 클릭
document.querySelector(".like-btn").addEventListener("click", async () => {
    const postId = document.body.getAttribute('data-post-id');
    const token = localStorage.getItem("token");

    try {
        const response = await fetch(`/posts/${postId}/like`, {
            method: "PATCH",
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${token}` // JWT 인증 추가
            }
        });

        if (response.status === 401) {
            alert("로그인이 필요합니다.");
            window.location.href = "/users/login";
            return;
        }
        if (!response.ok) throw new Error("좋아요 요청 실패");

        const updatedPost = await response.json();
        document.querySelector(".like-btn").innerHTML = `👍 ${updatedPost.likeCount}`;
    } catch (error) {
        console.error(error);
        alert("이미 좋아요를 누른 게시물입니다.");
    }
});

// 게시글 삭제
document.querySelector(".delete-btn").addEventListener("click", async () => {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    const postId = document.body.getAttribute('data-post-id');
    const token = localStorage.getItem("token");

    try {
        const response = await fetch(`/posts/${postId}`, {
            method: "DELETE",
            headers: { Authorization: `Bearer ${token}` } // JWT 인증 추가
        });

        if (response.status === 401) {
            alert("로그인이 필요합니다.");
            window.location.href = "/users/login";
            return;
        }
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
    const token = localStorage.getItem("token");
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
                Authorization: `Bearer ${token}` // JWT 인증 추가
            },
            body: JSON.stringify({ content: commentContent })
        });

        if (response.status === 401) {
            alert("로그인이 필요합니다.");
            window.location.href = "/users/login";
            return;
        }
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

    comments.forEach(renderNewComment);
}
// 댓글 추가 기능 (JWT 적용)
async function addComment() {
    const postId = document.body.getAttribute('data-post-id');
    const token = localStorage.getItem("token");
    const commentInput = document.querySelector(".comment-input");
    const commentContent = commentInput.value.trim();

    if (!commentContent) {
        alert("댓글을 입력하세요.");
        return;
    }

    if (!token) {
        alert("로그인이 필요합니다.");
        window.location.href = "/users/login";
        return;
    }

    try {
        const response = await fetch(`/posts/${postId}/comments`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}` // JWT 추가
            },
            body: JSON.stringify({ content: commentContent }) // userId 제거 (서버에서 JWT로 추출)
        });

        if (response.status === 401) {
            alert("인증이 필요합니다. 다시 로그인해주세요.");
            localStorage.removeItem("token");
            window.location.href = "/users/login";
            return;
        }
        if (!response.ok) throw new Error("댓글 등록 실패");

        const newComment = await response.json();
        commentInput.value = ""; // 입력 필드 초기화
        window.location.reload();
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

    comments.forEach(renderNewComment);
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
