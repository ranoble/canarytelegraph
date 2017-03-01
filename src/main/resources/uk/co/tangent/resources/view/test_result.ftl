<html>
<head>
    <script
  src="https://code.jquery.com/jquery-3.1.1.min.js"
  integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
  crossorigin="anonymous"></script>
    <!-- Custom styles for this template -->
    <link href="/static/starter-template.css" rel="stylesheet">
    <script src="/static/ace/ace.js" type="text/javascript" charset="utf-8"></script>
</head>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    
    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
    
    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
    
    

<body>

    <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">Project name</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="#about">About</a></li>
            <li><a href="#contact">Contact</a></li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </nav>

    <div class="container">
        <div class="row">

        <div class="col-sm-8">
            <h2>Test Results</h2>
            
            <#list testResults as result>
            <table class="table">
            <tr><th>Test</th><td>${result.test.id}</td></tr>
            <tr><th>Lane</th><td>${result.lane.id}</td></tr>
            <tr><th>Date</th><td>${result.written}</td></tr>
            <tr><th>Healthy?</th><td>${result.healthy?then('Yes', 'No')}</td></tr>
            <tr><th colspan=2>Step Results</th></tr>
            <tr><td colspan=2>
                <#assign resultList = result.getList()>
                <#list resultList as stepTest>
                    <p>${stepTest.confirmation.name}</p>
                    <p>Succeeded: ${stepTest.successful?then('Yes', 'No')}</p>
                    <p>${stepTest.message}</p>
                </p>
                </#list>
            </td></tr>
            </table>
            
            </#list>
        </div>
    </div>
        
</body>
</html>