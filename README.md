# aex-strategy-service..

Strategy microservice provides Strategy Management capability for AEX
[Architecture](https://confluence.walmart.com/display/APREC/AEX+Size+and+Pack)

* Dev: hostname
* Stg: hostname
* Prod: hostname

# Run in local with profile=local - SetUp Guide : 

## **Prerequisite**

### Make sure you have access to :

* [Hashicorp/Vault](https://wmlink/hashicorp)
* [Ccm2](https://wmlink/ccm2)
* Dev DB 
## To run jar you need following you need following args/ vm options
    -Dspring.profiles.active=local
    -Dserver.port= Any port
    -DsqlServer.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
    -Dsql.username= get it from https://wmlink/hashicorp -> secret -> apparel-precision-kitt -> aex-strategy-service -> dev -> sqlServer.username
    -Dsql.password= get it from https://wmlink/hashicorp -> secret -> apparel-precision-kitt -> aex-strategy-service -> dev -> sqlServer.password
    -Dccm.configs.dir= path of CCM folder in your local  <From kitt.yml find version of CCM and download that version of CCM folder from : https://wmlink/ccm2  -> AEX_STRATEGY_SERVICE -> <Vesrion mentoned in CCM> -> Download folder by clicking in work offline>
    -DmidasApi.authorization= fill content above-mentioned files with values mentioned in  : [hashicorp](https://wmlink/hashicorp) -> secret -> apparel-precision-kitt -> aex-strategy-service -> dev -> midasApi.authorization

