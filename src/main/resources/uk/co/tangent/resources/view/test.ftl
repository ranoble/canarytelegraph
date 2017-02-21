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

            <h1>Test Form</h1>
            
            <p>
              <form method="POST" action="/test">
                  <div class="form-group">
                    <label for="name">Test Name</label>
                    <input type="text" class="form-control" id="name" name="name" placeholder="Test Name" />
                    <input type="hidden" class="definition" name="definition" id="definition" />
                  </div>
                  <div class="form-group-lg" style="height: 330px">
                    <label for="editor">Test Definition</label>
                    <div class="form-control" style="height: 300px" id="editor"></div>
                  </div>
                  <div class="form-group">
                    <input type="submit" class="btn btn-lg btn-primary" value="Submit" />
                  </div>
              </form>
            </p>
          </div>
        </div>
    </div>

    <script>
    (function ($) {
        $.fn.serializeFormJSON = function () {
    
            var o = {};
            var a = this.serializeArray();
            $.each(a, function () {
                if (o[this.name]) {
                    if (!o[this.name].push) {
                        o[this.name] = [o[this.name]];
                    }
                    o[this.name].push(this.value || '');
                } else {
                    o[this.name] = this.value || '';
                }
            });
            return o;
        };
    })(jQuery);
    $(function() {
        
        window.editor = ace.edit("editor");
        editor.setTheme("ace/theme/monokai");
        editor.getSession().setMode("ace/mode/yaml");
        
        $('form').submit(function (e) {
            e.preventDefault();
            $('.definition').val(editor.getSession().getValue());
            var data = $(this).serializeFormJSON();
            $form = $(this);
            $.ajax({
              type: "POST",
              contentType: 'application/json',
              url: $form.attr('action'),
              data: JSON.stringify(data),
              done: function(data) {
                console.log(data);
              },
              dataType: 'json'
            });
        });
           
    });
        
    </script>
</body>
</html>