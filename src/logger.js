logger = (function(){
    var instance = null;
    function Constructor(){
        this.div = document.createElement("console");
        this.div.id = "console";
        this.div.style.cssText = "filter:alpha(opacity=80);position:absolute;top:0px;left:0px;width:100%;border:1px solid #ccc;background:#eee;";
        document.body.appendChild(this.div);
    }
    Constructor.prototype = {
        log : function(str){
            var p = document.createElement("p");
            p.innerHTML = str;
            this.div.appendChild(p);
        }
    }
    function getInstance(){
        if(instance == null){
            instance =  new Constructor();
        }
        return instance;
    }
    return getInstance();
})();