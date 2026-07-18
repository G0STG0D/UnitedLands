package org.unitedlands.unitedlands.classes.webservices;

import io.undertow.util.AttachmentKey;

import java.util.Map;

import io.jsonwebtoken.Claims;

public final class AttachmentKeys {
    public static final AttachmentKey<Claims> CLAIMS  = AttachmentKey.create(Claims.class);
    public static final AttachmentKey<String> SUBJECT = AttachmentKey.create(String.class);
    @SuppressWarnings("rawtypes")
    public static final AttachmentKey<Map> PATH_PARAMS = AttachmentKey.create(Map.class);

    private AttachmentKeys() {}
}