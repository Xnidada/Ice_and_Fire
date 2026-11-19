#!/usr/bin/env bash
set -euo pipefail

tools_root="/opt/minecraft-tools"
gradle_home="$tools_root/gradle-home"
java_home="$tools_root/jdks/jdk-25.0.4.1+1"
gradle_bin="$tools_root/gradle/gradle-9.6.1/bin/gradle"

for required_path in "$gradle_home" "$java_home" "$gradle_bin"; do
    if [[ ! -e "$required_path" ]]; then
        echo "Missing Minecraft build tool: $required_path" >&2
        exit 1
    fi
done

export GRADLE_USER_HOME="$gradle_home"
export JAVA_HOME="$java_home"
export PATH="$java_home/bin:$(dirname "$gradle_bin"):$PATH"

if [[ "${BUILD_ONLINE:-0}" == "1" ]]; then
    export http_proxy="${http_proxy:-http://192.168.30.118:7890}"
    export https_proxy="${https_proxy:-http://192.168.30.118:7890}"
    export HTTP_PROXY="$http_proxy"
    export HTTPS_PROXY="$https_proxy"
    offline_args=()
else
    offline_args=(--offline)
fi

task="${1:-build}"
"$gradle_bin" --no-daemon "${offline_args[@]}" "$task"

if [[ "$task" == "build" || "$task" == "assemble" ]]; then
    mod_version="$(awk -F '=' '/^[[:space:]]*mod_version[[:space:]]*=/{gsub(/[[:space:]]/, "", $2); print $2; exit}' gradle.properties)"
    if [[ -z "$mod_version" ]]; then
        echo "Could not read mod_version from gradle.properties" >&2
        exit 1
    fi
    artifact="build/libs/Ice and Fire-${mod_version}.jar"
    if [[ -f "$artifact" ]]; then
        echo "Built artifact: $artifact"
    else
        echo "Build succeeded, but expected artifact was not found: $artifact" >&2
        exit 1
    fi
fi
