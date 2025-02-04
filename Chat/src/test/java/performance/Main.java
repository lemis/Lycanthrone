package performance;

import com.bigsonata.swarm.Locust;
import io.vertx.core.json.Json;
import performance.locust.ApiCron;
import performance.locust.SignUpCron;
import performance.locust.WSCron;
import performance.model.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiIxOGEyZDljNy1hNTlkLTQwM2YtYjU4Mi1iYzA0OTk1Yzc5YjYiLCJpYXQiOjE1MzQyMzYwMjksImV4cCI6MTUzNDQwODgyOSwiYXVkIjoiVXNlciIsImlzcyI6IkhleSBDb3JwLiJ9.IgdPlO74fuRcN202DFCUC63fDwQBeHzFYrEgjTxtaZY";
        String jwtString = "Bearer " + jwt;
        String serverHost = "http://localhost:8080/";
        String wsHost = "ws://localhost:8090/";
        Locust locust = Locust.Builder.newInstance()
            .setMasterHost("localhost")
            .setMasterPort(5557)
            //.setMaxRps(1000)
            .build();

        ApiCron getChatList = new ApiCron(serverHost + "API/protected/chatlist", jwtString, "GET", "Get Chat List");
        ApiCron getAddressBook = new ApiCron(serverHost + "API/protected/addressbook", jwtString, "GET", "Get Address Book");
        ApiCron getProfile = new ApiCron(serverHost + "API/protected/user", jwtString, "GET", "Get Profile");

        UserRequest userRequest = new UserRequest();
        userRequest.setUserName("vcthanh24");
        userRequest.setPassword("123");
        ApiCron login = new ApiCron(serverHost + "signin", jwtString, "POST", "Sign In", Json.encodePrettily(userRequest));
        SignUpCron register = new SignUpCron(serverHost + "API/public/user", jwtString, "POST", "Register");

        ChangeStatusRequest changeStatusRequest = new ChangeStatusRequest();
        changeStatusRequest.setStatus("Test");
        ApiCron changeStatus = new ApiCron(serverHost + "API/protected/user", jwtString, "POST", "Change Status", Json.encodePrettily(changeStatusRequest));

        ChatMessageRequest chatMessageRequest = new ChatMessageRequest();
        chatMessageRequest.setType(IWsMessage.TYPE_CHAT_MESSAGE_REQUEST);
        chatMessageRequest.setMessage("Test Message");
        chatMessageRequest.setSessionId("-1");
        chatMessageRequest.setGroupChat(true);
        List<String> users = new ArrayList<>();
        users.add("nthnhung");
        users.add("user20");
        chatMessageRequest.setUsernames(users);
        WSCron sendExistedSession = new WSCron(wsHost, jwt, Json.encodePrettily(chatMessageRequest), "Send a Message");

        ChatContainerRequest chatContainerRequest = new ChatContainerRequest();
        chatContainerRequest.setType(IWsMessage.TYPE_CHAT_ITEM_REQUEST);
        chatContainerRequest.setSessionId("6b2f6a05-c52c-4a12-a036-4258e588f390");

        WSCron getChatSession = new WSCron(wsHost, jwt, Json.encodePrettily(chatContainerRequest), "Chat Session");
        locust.run(getChatList, getAddressBook, getProfile, changeStatus);

        //locust.run(getChatSession);
    }
}
