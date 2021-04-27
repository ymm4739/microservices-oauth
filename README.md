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
## github action实现ci/cd
在源代码根目录新建.github/workflows目录，目录中所有yml文件都被是为一个流水线，github会自动执行该流水线。
ci.yml文件内容：
```$xslt
# This workflow will build a Java project with Maven
# For more information see: https://help.github.com/actions/language-and-framework-guides/building-and-testing-java-with-maven

name: Java CI with Maven and CD with Docker

on:
  push:
    branches: [ master ]
  pull_request:
    branches: [ master ]
env:
  NAME: oauth
  EXPOSEPORT: 8100
jobs:
  build:

    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 1.8
        uses: actions/setup-java@v1
        with:
          java-version: 1.8
      - name: Package
        run: mvn clean package -DskipTest
      - name: Show jar
        run: ls -al target
      - name: Deploy
        uses: cross-the-world/ssh-scp-ssh-pipelines@latest
        env:
          WELCOME: "stop former container"
          LASTSSH: "start new container"
        with:
          host: ${{ secrets.DEPLOY_HOST }}
          user: ${{ secrets.DEPLOY_USERNAME }}
          pass: ${{ secrets.DEPLOY_PASSWORD }}
          port: ${{ secrets.DEPLOY_PORT }}
          connect_timeout: 10s
          first_ssh: |-
            docker ps
            echo $WELCOME
            mkdir -p /opt/deploy/$NAME
            docker stop $NAME
            docker container rm $NAME
            docker image rm $NAME
          scp: |-
            './target/*.jar' => /opt/deploy/$NAME
            './docker/Dockerfile' => /opt/deploy/$NAME
          last_ssh: |-
            echo $LASTSSH
            cd /opt/deploy/$NAME
            docker build -t $NAME .
            docker run --name $NAME -p $EXPOSEPORT:$EXPOSEPORT -d $NAME
            docker ps
```
该文件为spring boot打包为jar在docker中运行的模版。
使用方法：
+ 修改env中NAME和EXPOSEPORT，EXPOSEPORT为访问端口server.port。
+ 在根目录新建docker文件夹，编写Dockerfile文件，内容为：
```$xslt
FROM openjdk:8u272-jre-slim
ADD oauth-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","-Duser.timezone=GMT+8","-Dspring.profiles.active=product","/app.jar"]
```
oauth-0.0.1-SNAPSHOT.jar为打包后的jar包
此处指定了product为激活的profile，需要在nacos网页中新建配置文件oauth-product.yml，文件内容为：
```$xslt
discovery:
  ip: 39.96.7.6
  weight: 1
```
ip为jar包实际运行的ip地址，weight为权重，值越大被访问的概率越大。
+ 在本项目对应的github的仓库的Setting中配置Secrets，新增4个Secret。
Secret名称分别为DEPLOY_HOST、DEPLOY_USERNAME、DEPLOY_PASWORD、DEPLOY_PORT，
对应的值分别为运行jar的远程机器ip地址，ssh登录该机器的用户名，ssh登录密码，ssh登录端口(22)
**该机器需要先安装docker，否则无法编译Dockerfile**
## 使用方法
本项目集成了github actions，fork本项目后，即可使用github提供的ci/cd，自动打包部署。
前置条件：
1. 远程服务器一台，开启22和8200端口，具有ssh登录用户名和密码，**服务器已安装docker**
2. 点击自己fork的仓库中的Setting，选择Secret，新增4个Secret,分别为DEPLOY_HOST、DEPLOY_USERNAME、DEPLOY_PASSWOR、DEPLOY_PORT，
对应的值分别为远程服务器ip地址、ssh登录用户名、ssh登录密码和ssh端口号即22。
3. 点击仓库中的Action，开启workflow功能，若页面无开启按钮代表已开启
4. 导入resources/db目录中的microservices-usercenter数据库
访问nacos配置中心，新建oauth-product.yml文件，文件内容为：
```$xslt
discovery:
  ip: 远程服务器ip 
  weight: 1
```
现在只需往仓库push或者pr，Action会自动执行，可点击Action查看具体运行情况。