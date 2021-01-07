#!/bin/sh

orgAndRepo="${GITHUB_REPOSITORY}"
prNumber=${1}
token="${2}"
depsOkLabel="${3}"
depsChangedLabel="${4}"
changeCommentMentions="${5}"
baseVersionFiles="${6}"
newVersionFiles="${7}"

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
