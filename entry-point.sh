#!/bin/sh

# Get the variables
pr=$(jq --raw-output .pull_request "$GITHUB_EVENT_PATH")
if [ "${pr}" = "null" ]; then
  pr=$(jq --raw-output .event.workflow_run.pull_requests[0].number "$GITHUB_EVENT_PATH")
  if [ "${pr}" = "null" ]; then
    echo This does not seem to be a pull request!
    exit 1
  fi
fi

orgAndRepo="${GITHUB_REPOSITORY}"
prNumber=$(jq --raw-output .pull_request.number "$GITHUB_EVENT_PATH")
token="${1}"
depsOkLabel="${2}"
depsChangedLabel="${3}"
changeCommentMentions="${4}"
baseVersionFiles="${5}"
newVersionFiles="${6}"

echo "=== Input variables: ==="
echo "token: ${token}"
echo "GitHub Org/Repo: ${orgAndRepo}"
echo "PR Number: ${prNumber}"
echo "Dependencies OK Label Name: ${depsOkLabel}"
echo "Dependencies Changed Label Name: ${depsChangedLabel}"
echo "Change Mentions: ${changeCommentMentions}"
echo "Base Version Files: ${baseVersionFiles}"
echo "New Version Files: ${newVersionFiles}"

echo "JAVA_HOME: ${JAVA_HOME}"
# By default this points to the jre, redirect to the jdk
export JAVA_HOME=/opt/ibm/java
echo "JAVA_HOME (adjusted): ${JAVA_HOME}"

######################################################################
# Grab dependencies
######################################################################

echo "Adjust version file paths"
tmp=""
for file in $(echo "${baseVersionFiles}" | sed "s/,/ /g"); do
  file="${GITHUB_WORKSPACE}/${file}"
  if [ -z "${tmp}" ]; then
    tmp="${file}"
  else
    tmp="${tmp},${file}"
  fi
done
baseVersionFiles="${tmp}"
echo "Base version files: ${baseVersionFiles}"

tmp=""
for file in $(echo "${newVersionFiles}" | sed "s/,/ /g"); do
  file="${GITHUB_WORKSPACE}/${file}"
  if [ -z "${tmp}" ]; then
    tmp="${file}"
  else
    tmp="${tmp},${file}"
  fi
done
newVersionFiles="${tmp}"
echo "New version files: ${newVersionFiles}"

java -jar \
  -Ddeptree.tool.reporter.github.deps-ok.label="${depsOkLabel}" \
  -Ddeptree.tool.reporter.github.deps-changed.label="${depsChangedLabel}" \
  -Ddeptree.tool.reporter.github.repo="${orgAndRepo}" \
  -Ddeptree.tool.reporter.github.pr="${prNumber}" \
  -Ddeptree.tool.reporter.github.token="${token}" \
  -Ddeptree.tool.reporter.github.trace=true \
  -Ddeptree.tool.reporter.github.change.mentions="${changeCommentMentions}" \
  -Ddeptree.tool.reporter.github.actions \
  /tool/dep-tree-reporter-github-labels.jar \
  "${baseVersionFiles}" "${newVersionFiles}" ||
  exit 1
