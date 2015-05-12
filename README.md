# ScriptDB

What is ScriptDB?
A language independent template-based system for executing SSH tasks. 

What is the motivation? 
It is very common while designing and developing a project that involves many MapReduce jobs to have many script files dedicated to the execution of such jobs
and the pipelines composed of these jobs. The management of these script files becomes a tougher task as the project progress. Moreover, many of these script files follow
a common pattern (e.g., check out the project from github, build the project, run the job). How can we make use of this common pattern and also 
able to save the configuration for a given job such that we can re-run the job later? 

With ScriptDB, we can achieve the following

1. Define a script template using <a href="http://stringtemplate.org">StringTemplate</a> engine. 
2. Scripts can be generated from the template and the values provided from the interface.
3. The generated scripts can be executed in a remote server.
4. Interact with any of the servers using the "Run command" option.

What is not there in TailorSwift?
<a href="https://github.com/feroshjacob/TailorSwift">TailorSwift</a> was designed with the similar motivation but the main limitation was the Eclipse IDE. In addition to this, the
ScriptDB is using a sophisticated template engine, hence the scripts can be in any language (e.g., Python, Bash)




