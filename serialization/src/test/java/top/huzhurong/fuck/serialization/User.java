package top.huzhurong.fuck.serialization;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/29
 */
@Getter
@Setter
@EqualsAndHashCode
public class User implements Serializable {
    private List<String> strings;
    private Map<String, String> maps;
    private Map<String, List<String>> listMap;
    private String name;
    private Date date;
}
