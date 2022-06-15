# simsim ip-filters

The simsim ip-filters is a simple and easy-to-use library which decide ip allow/deny.

## Usage

### Repository

#### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.madvirus</groupId>
    <artifactId>simsim-ipfilters</artifactId>
    <version>1.0.0</version>
</dependency>
```
#### Gradle
```
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.madvirus:simsim-ipfilters:1.0.0'
}
```
#### Create and use IpFilter

```
IpFilter ipFilter = IpFilter.builder()
        .allowIp("1.2.3.4") // add ip pattern into allowed set 
        .allowIp("1.2.3.5") // add ip pattern into allowed set 
        .allowIps(Arrays.asList("1.2.3.2", "1.2.3.3")) // add ip patterns into allowed set
        .denyIp("1.2.3.5") // add pattern  into denied set
        .denyIps(Arrays.asList("1.2.3.6", "1.2.3.7")) // add patterns into denied set
        .denyByDefault() // deny ip if both allowed set and denied set do not contains that
        // .allowByDefault() // allow ip if both allowed set and denied set do not contains that
        .denyFirst() // deny ip if both allowed set and denied set contains ip
        // .allowFirst() // allow ip if allowed set and denied set contains ip
        .build();

ipFilter.allow("1.2.3.2"); // true --> in allow ip
ipFilter.allow("1.2.3.4"); // true --> in allow ip
ipFilter.allow("1.2.3.5"); // false --> deny first
ipFilter.allow("1.2.3.8"); // false --> deny by default
```

### supported ip pattern

| pattern                    | example                   |
|----------------------------|---------------------------|
| single ip                  | 1.2.3.4<br/>5.6.7.8       | 
| asterisk(wildcard) pattern | 1.2.3.*<br/>1.2.*<br/>1.* |
| CIDR pattern               | 1.2.3.0/24<br/>1.2.2/22   |

## IpSet implementations

simsim ip-filters use IpSet for manage ip list.
There are 3 implementations of IpSet.

| IpSet       | Desc                                   | IP Set Size                                   |
|-------------|----------------------------------------|-----------------------------------------------|
| TreeIpSet   | Use tree structure for managing ip set | fast for large ip set                         |
| HashIpSet   | Use hash set for managing ip set       | fast for large ip set                         |
| SimpleIpSet | Use list for managing ip set           | suitable for small ip patterns (count < 5000) |

### configure IpSet type

```
// IpSetType.TREE / IpSetType.HASH / IpSetType.SIMPLE
IpFilter filter = IpFilter.builder()
        .allowIpSetType(IpFilter.IpSetType.TREE) // default
        .denyIpSetType(IpFilter.IpSetType.TREE) // default
        .build();
```
