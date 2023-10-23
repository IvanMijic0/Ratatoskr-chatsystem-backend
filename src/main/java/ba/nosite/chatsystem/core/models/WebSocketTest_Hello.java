package ba.nosite.chatsystem.core.models;

public class WebSocketTest_Hello {
    private String name;

    public WebSocketTest_Hello() {
    }

    public WebSocketTest_Hello(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
