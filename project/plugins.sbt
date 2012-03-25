resolvers += "sonatype.repo" at "https://oss.sonatype.org/content/groups/public"

resolvers += Resolver.url("sbt-plugin-releases",
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(
    Resolver.ivyStylePatterns)

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.10"))

addSbtPlugin("eu.getintheloop" %% "sbt-cloudbees-plugin" % "0.4.0")
