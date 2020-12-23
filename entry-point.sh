#!/bin/sh

# Get the variables
pr=$(jq --raw-output .pull_request "$GITHUB_EVENT_PATH")
if [ "${pr}" == "null" ]; then
    echo This does not seem to be a pull request!
    exit 1
fi

orgAndRepo="${GITHUB_REPOSITORY}"
prNumber=$(jq --raw-output .pull_request.number "$GITHUB_EVENT_PATH")
token="${1}"
modules="${2}"
depsOkLabel="${3}"
depsChangedLabel="${4}"
changeCommentMentions="${5}"

echo "=== Input variables: ==="
echo "token: ${token}"
echo "GitHub Org/Repo: ${orgAndRepo}"
echo "Dependency Tree Modules: ${modules}"
echo "PR Number: ${prNumber}"
echo "Dependencies OK Label Name: ${depsOkLabel}"
echo "Dependencies Changed Label Name: ${depsChangedLabel}"
echo "Change Mentions: ${changeCommentMentions}"

echo "JAVA_HOME: ${JAVA_HOME}"

# By default this points to the jre, redirect to the jdk
export JAVA_HOME=/opt/ibm/java

echo "JAVA_HOME (adjusted): ${JAVA_HOME}"

######################################################################
# Grab dependencies
######################################################################


echo "Grabbing PR dependencies"
cd ${GITHUB_WORKSPACE}/pr || exit 1
i=0
newVersionFiles=""
for module in $(echo "${modules}" | sed "s/,/ /g")
do
    newVersionFile="/_new-versions-$i.txt"
    mvn -B dependency:tree -pl "${module}" -DoutputFile="${newVersionFile}" || exit 1
    if ((i > 0)); then
        newVersionFiles="${newVersionFiles},${newVersionFile}"
    else
        newVersionFiles="${newVersionFile}"
    fi
    i=$((i + 1))
done
echo "New version files: ${newVersionFiles}"

echo "Grabbing baseline dependencies"
cd ${GITHUB_WORKSPACE}/base || exit 1
i=0
baseVersionFiles=""
for module in $(echo "${modules}" | sed "s/,/ /g")
do
    baseVersionFile="/_base-versions-$i.txt"
    mvn -B dependency:tree -pl "${module}" -DoutputFile="${baseVersionFile}" || exit 1
    if ((i > 0)); then
        baseVersionFiles=$baseVersionFiles,$baseVersionFile
    else
        baseVersionFiles=$baseVersionFile
    fi
    i=$((i + 1))
done
echo "Base version files: "$baseVersionFiles

cd ${GITHUB_WORKSPACE}
java -jar \
    -Ddeptree.tool.reporter.github.deps-ok.label="${depsOkLabel}" \
    -Ddeptree.tool.reporter.github.deps-changed.label="${depsChangedLabel}" \
    -Ddeptree.tool.reporter.github.repo="${orgAndRepo}" \
    -Ddeptree.tool.reporter.github.pr="${prNumber}"  \
    -Ddeptree.tool.reporter.github.token="${token}" \
    -Ddeptree.tool.reporter.github.trace=true \
    -Ddeptree.tool.reporter.github.change.mentions="${changeCommentMentions}" \
    -Ddeptree.tool.reporter.github.actions \
    /tool/dep-tree-reporter-github-labels.jar \
    $baseVersionFiles $newVersionFiles \
     || exit 1