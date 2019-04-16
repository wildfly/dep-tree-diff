#!/bin/sh

# The intent is for this to be kicked off from the folder containing a git clone that we want to check the deps for

echo "===== Dep Tree analyser ====="
originalDir=$PWD
script=$0
toolRelativeUrl=$1
mvnCmd=$2
orgAndRepo=$3
targetBranch=$4
modules=$5
prNumber=$6
depsOkLabel=$7
depsChangedLabel=$8
token=$9
changeMentions=${10}

echo "=== Input variables: ==="
echo "Git clone directory: " $originalDir
echo "Script: " $script
echo "Tool clone relative url: " $toolRelativeUrl
echo "Maven command: " $mvnCmd
echo "GitHub Org/Repo: " $orgAndRepo
echo "Baseline version branch: " $targetBranch
echo "Dependency Tree Modules: " $modules
echo "PR Number: " $prNumber
echo "Dependencies OK Label Name: " $depsOkLabel
echo "Dependencies Changed Label Name: " $depsChangedLabel
echo "=== Environment variables ==="
echo "Change Mentions: " $TOOL_CHANGE_MENTIONS
echo "Settings xml: " $TOOL_MVN_SETTINGS_XML
echo

mavenSettings=""
if [ -z "$TOOL_MVN_SETTINGS_XML" ]; then
    echo "no maven settings override"
else
    mvnCmd="$mvnCmd -s $TOOL_MVN_SETTINGS_XML ";
    echo "Overriding maven command to use alternative maven settings:";
    echo $mvnCmd;
fi


echo "Grabbing original dependencies"
i=0
newVersionFiles=""
for module in $(echo $modules | sed "s/,/ /g")
do
    newVersionFile=$originalDir/_new-versions-$i.txt
    $mvnCmd dependency:tree -pl $module -DoutputFile=$newVersionFile
    if (($i > 0)); then
        newVersionFiles=$newVersionFiles,$newVersionFile
    else
        newVersionFiles=$newVersionFile
    fi
    i=$(($i + 1))
done

echo "New version files: "$newVersionFiles


echo "Fetching origin"
git fetch origin

echo "Checking out the $targetBranch branch"
git checkout origin/$targetBranch
echo "Building the $targetBranch branch so we get the baseline dependencies"
$mvnCmd clean install -DskipTests -pl $modules -am


echo "Grabbing baseline dependencies"
i=0
baseVersionFiles=""
for module in $(echo $modules | sed "s/,/ /g")
do
    baseVersionFile=$originalDir/_base-versions-$i.txt
    $mvnCmd dependency:tree -pl $module -DoutputFile=$baseVersionFile
    if (($i > 0)); then
        baseVersionFiles=$baseVersionFiles,$baseVersionFile
    else
        baseVersionFiles=$baseVersionFile
    fi
    i=$(($i + 1))
done
echo "Base version files: "$baseVersionFiles

echo "java -jar
    -Ddeptree.tool.reporter.github.deps-ok.label=$depsOkLabel \
    -Ddeptree.tool.reporter.github.deps-changed.label=$depsChangedLabel \
    -Ddeptree.tool.reporter.github.repo=$orgAndRepo \
    -Ddeptree.tool.reporter.github.pr=$prNumber  \
    -Ddeptree.tool.reporter.github.token=--HIDDEN-- \
    -Ddeptree.tool.reporter.github.trace=true \
    -Ddeptree.tool.reporter.github.change.mentions=$changeMentions \
    $toolRelativeUrl/reporters/github-labels/target/dep-tree-reporter-github-labels.jar \
    $baseVersionFiles $newVersionFiles"

java -jar \
    -Ddeptree.tool.reporter.github.deps-ok.label=$depsOkLabel \
    -Ddeptree.tool.reporter.github.deps-changed.label=$depsChangedLabel \
    -Ddeptree.tool.reporter.github.repo=$orgAndRepo \
    -Ddeptree.tool.reporter.github.pr=$prNumber  \
    -Ddeptree.tool.reporter.github.token=$token \
    -Ddeptree.tool.reporter.github.trace=true \
    -Ddeptree.tool.reporter.github.change.mentions=$changeMentions \
    $toolRelativeUrl/reporters/github-labels/target/dep-tree-reporter-github-labels.jar \
    $baseVersionFiles $newVersionFiles
