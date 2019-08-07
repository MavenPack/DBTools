# DBTools

gradle:
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
	implementation 'com.github.MavenPack:DBTools:0.1'
}
```

Maven:
```
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>

<dependency>
    <groupId>com.github.MavenPack</groupId>
    <artifactId>DBTools</artifactId>
    <version>0.0.4</version>
</dependency>
```
