var stompClient = null;
var jwtToken = null;

// JWT 디코딩 함수
function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
        atob(base64)
            .split('')
            .map(c => `%${('00' + c.charCodeAt(0).toString(16)).slice(-2)}`)
            .join('')
    );
    return JSON.parse(jsonPayload);
}

// WebSocket 연결 관리
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

// WebSocket 연결
function connect() {
    if (!jwtToken) {
        alert("Please log in first!");
        return;
    }

    // JWT에서 idx 추출
    const idx = parseJwt(jwtToken).idx;
    console.log("Extracted idx:", idx);

    const socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        // idx 기반 경로 구독
        const subscriptionPath = `/socket/${idx}`;
        stompClient.subscribe(subscriptionPath, function (message) {
            // 서버에서 전송된 메시지를 화면에 표시
            showGreeting(message.body);
        });
        console.log(`Subscribed to: ${subscriptionPath}`);
    });
}

// WebSocket 연결 해제
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

// 메시지 출력
function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

// 로그인
async function login() {
    const username = $("#username").val();
    const password = $("#password").val();

    if (!username || !password) {
        alert("Please enter username and password!");
        return;
    }

    try {
        const response = await fetch("/members/sign-in", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const data = await response.json();
            jwtToken = data.accessToken; // JWT 토큰 저장
            console.log("Login successful! Token:", jwtToken);

            // 로그인 후 WebSocket 연결
            connect();
        } else {
            alert("Login failed!");
            console.error("Login error:", await response.text());
        }
    } catch (error) {
        console.error("Login error:", error);
    }
}

// 메시지 전송
function sendName() {
    if (!stompClient) {
        alert("WebSocket is not connected!");
        return;
    }
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

// 이벤트 핸들러 설정
$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    // 로그인 버튼 클릭
    $("#loginButton").click(function () { login(); });

    // 메시지 전송
    $("#send").click(function () { sendName(); });

    // WebSocket 연결 해제
    $("#disconnect").click(function () { disconnect(); });
});
