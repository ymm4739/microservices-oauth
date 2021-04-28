# oauth
OAuth2单点登陆微服务，实现认证与鉴权，生成JWT
## 关键代码
### AuthorizationServerConfig 授权服务器的配置
+ 配置Oauth的客户端数据源
```$xslt
// 配置客户端数据源为数据库的oauth_client_details表
    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
//        ClientDetailsServiceBuilder builder = new ClientDetailsServiceBuilder();
//        builder.inMemory().withClient(clientConfig.getId()).secret(clientConfig.getSecret())
//                .scopes(clientConfig.getScope()).accessTokenValiditySeconds(clientConfig.getValidateSecond())
//                .authorizedGrantTypes(clientConfig.getGrantTypes()).resourceIds(clientConfig.getResourceId());
//        //builder.clients(clients);
//        clients.inMemory()
//                .withClient("password")
//                .authorizedGrantTypes("password", "refresh_token") //授权模式为password和refresh_token两种
//                .accessTokenValiditySeconds(1800) // 配置access_token的过期时间
//                .resourceIds("rid") //配置资源id
//                .scopes("all")
//                .secret(passwordEncoder.encode("wangu123!@#")); //加密后的密码
        clients.jdbc(dataSource);
    }
```
此处是利用数据表oauth_client_details配置,具体字段查看数据库。注释部分是在内存中配置。
+ 配置token为JWT
```
       @Override
       public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
           TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
           List<TokenEnhancer> tokenEnhancerList = new ArrayList<TokenEnhancer>();
           tokenEnhancerList.add(jwtTokenEnhancer);
           tokenEnhancerList.add(accessTokenConverter());
           tokenEnhancerChain.setTokenEnhancers(tokenEnhancerList);
           endpoints.accessTokenConverter(accessTokenConverter())
                   .tokenEnhancer(tokenEnhancerChain)
                   //.tokenStore(redisTokenStore) //配置令牌的存储（这里存放在内存中）
                   .authenticationManager(authenticationManager)
                   .userDetailsService(userDetailsService);
           // .exceptionTranslator(translator);
       }
```
+ 配置JWT加密
```
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
    }
```
+ 配置token的访问权限
``` 
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        // 表示支持 client_id 和 client_secret 做登录认证
        security.allowFormAuthenticationForClients();
        security.checkTokenAccess("isAuthenticated()");
        security.tokenKeyAccess("permitAll()");
        // security.accessDeniedHandler(restAuthExceptionHandler);
        // security.authenticationEntryPoint(restAuthExceptionHandler);
        // security.
    }
```
### JwtTokenEnhancer JWT头部内容
```
   @Component
   public class JwtTokenEnhancer implements TokenEnhancer {
   
       public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
           UserInfo userInfo = (UserInfo) oAuth2Authentication.getPrincipal();
           Map<String, Object> map = new HashMap<String, Object>();
           map.put("uid", userInfo.getId());
           map.put("authorities", new HashSet(userInfo.getAuthorities()));
           ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(map);
           return oAuth2AccessToken;
       }
   }
```
### OAuth2TokenDto JWT的格式
```$xslt
@Data
public class OAuth2TokenDto implements Serializable {
    private static final Long serialVersionId = 1L;

    private String accessToken;
    private String refreshToken;
    private Integer expiration;
    private String tokenType = "bearer";
}
```
### AuthController 认证
+ 登录认证
```$xslt
    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST)
    public ApiResult postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        OAuth2TokenDto oauth2TokenDto = new OAuth2TokenDto();
        oauth2TokenDto.setAccessToken(oAuth2AccessToken.getValue());
        oauth2TokenDto.setRefreshToken(oAuth2AccessToken.getRefreshToken().getValue());
        oauth2TokenDto.setExpiration(oAuth2AccessToken.getExpiresIn());
        oauth2TokenDto.setTokenType(oAuth2AccessToken.getTokenType());

        return new ApiResult(0, "操作成功", oauth2TokenDto);
    }
```
+ 获取JWT加密公钥解密JWT
```$xslt
    @GetMapping("/rsa/publicKey")
    public Map<String, Object> getKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }
```
