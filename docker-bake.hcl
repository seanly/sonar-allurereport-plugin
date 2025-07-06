variable "VERSION" {
  default = "9.9"
}

variable "FIXID" {
  default = "1"
}

group "default" {
  targets = ["sonar-allurereport-plugin"]
}

target "sonar-allurereport-plugin" {
    labels = {
        "cloud.opsbox.author" = "seanly"
        "cloud.opsbox.image.name" = "sonar-allurereport-plugin"
        "cloud.opsbox.image.version" = "${VERSION}"
        "cloud.opsbox.image.fixid" = "${FIXID}"
    }
    dockerfile = "Dockerfile"
    context  = "./"
    args = {
        VERSION="${VERSION}"
    }
    platforms = ["linux/amd64"]
    tags = ["seanly/appset:sonar-allurereport-plugin-${VERSION}"]
    output = ["type=image,push=true"]
}
