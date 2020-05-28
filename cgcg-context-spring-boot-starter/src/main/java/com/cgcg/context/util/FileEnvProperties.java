package com.cgcg.context.util;

import com.cgcg.context.SpringContextHolder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
public class FileEnvProperties {
    private String url;
    private String key;
    private String secret;
    private String bucket;
    private String prefix;

    public static FileEnvProperties getInstance() {
        return new FileEnvProperties() {{
            setUrl(SpringContextHolder.getProperty("aws.file_url", String.class));
            setKey(SpringContextHolder.getProperty("aws.file_key",String.class));
            setBucket(SpringContextHolder.getProperty("aws.file_bucket",String.class));
            setSecret(SpringContextHolder.getProperty("aws.file_secret",String.class));
            setPrefix(SpringContextHolder.getProperty("aws.file_env",String.class));
        }};
    }

    public static FileEnvProperties getInstance(String url,String key,String secret,String bucket,String prefix) {
        return new FileEnvProperties() {{
            setUrl(url);
            setKey(key);
            setBucket(bucket);
            setSecret(secret);
            setPrefix(prefix);
        }};
    }
}
