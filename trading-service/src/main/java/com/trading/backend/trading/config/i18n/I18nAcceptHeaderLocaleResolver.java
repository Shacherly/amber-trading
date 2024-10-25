package com.google.backend.trading.config.i18n;

import com.google.backend.trading.model.user.UserInfo;
import com.google.backend.trading.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @author david.chen
 * @date 2021/11/8 10:22
 * 自定义languageHeader解析
 */
@Slf4j
public class I18nAcceptHeaderLocaleResolver extends AcceptHeaderLocaleResolver {

    @Value("${language.header}")
    private String languageHeader;

    @Autowired
    private UserService userService;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String l = request.getHeader(languageHeader);
        if (log.isDebugEnabled()) {
            log.debug("resolve locale, value = {}, from header {}", l, languageHeader);
        }
        if (StringUtils.isEmpty(l)) {
            return super.resolveLocale(request);
        }
        String[] split = l.split("-");
        Locale locale = new Locale(split[0], split[1]);

        Object object = request.getAttribute(UserInfo.CURRENCY_USER_TAG);
        if (object instanceof UserInfo) {
            userService.setLocale(((UserInfo)object).getUid(), locale);
        }
        return locale;
    }

}
