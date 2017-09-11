package com.eqan.utils.json;

import java.io.IOException;

import com.eqan.web.model.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class UserSerializer extends StdSerializer<User> {

    private static final long serialVersionUID = 8295448136451057720L;

    public UserSerializer() {
        this(User.class);
    }
    public UserSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User user, JsonGenerator jGen, SerializerProvider provider) throws IOException {
        jGen.writeStartObject();
        jGen.writeStringField("email", user.getEmail());
        jGen.writeStringField("password", user.getPassword());
        jGen.writeEndObject();
    }

}
