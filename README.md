# dep-tree-diff
Tool to determine changes to the dependency tree brought in by a pull request/feature branch

The intent is to set up automation of checking pull requests. The envisaged flow is that you have a job which does something along the lines of:
1) Fetching the pull request
2) Building the source code (skipping tests to keep it fast)
3) Running `mvn dependency:tree` and storing the output to `/some/where/changed-deps.txt`
4) Checking out the target branch of the pull request, and repeating steps 2 and 3, saving the output to `/some/where/original-deps.txt`
5) Run `java -jar dep-tree-diff-core-<VERSION>.jar /some/where/original-deps.txt /some/where/changed-deps.txt` and get reports of:
    * Dependencies which were added
    * Dependencies which were removed
    * Dependencies which look like they have had a major version bump
    


By default this app only outputs things to System.out.println(). It is extensible, in that you can create your own implementations of `org.wildfly.deptreediff.core.DepTreeDiffReporter` and make that available on the classpath via a ServiceLoader. Your `DepTreeDiffReporter` implementation will then receive the differences mentioned and can do more interesting stuff with that (for example label the pull reqeust to indicate what sort of changes happened).

# Action
This is also available as a GitHub Action. See the [Example Workflow Yaml](example-workflow.yml) for how to use it, and
the [Action Yaml](action.yml) for some information about the parameters.

Note that the example Yaml does the cloning of the two versions of the pull request being tested, and a build of each before calling the this action from the (example-workflow.yml) workflow triggered by the `workflow_run` event once the main workflow completes. This is mainly to take advantage of the Maven Repository caching available when doing it this way.

## Releasing the Action

This is currently a manual process, and we're presently just using the `latest` tag (in action.yml) of the Docker image providing the tooling.

1. Update your local copy of the branch
2. Run `mvn install` to build the Java tool
3. Build the docker image: `docker build . -t quay.io/kabirk/dep-tree-diff`
4. Push the image (having logged into quay.io first): `docker push quay.io/kabirk/dep-tree-diff`