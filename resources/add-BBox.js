
(function (){
	ep = Element.prototype;
	
	ep.getBBox = function(){
		var w = 10;
	  if (this.tagName.toUpperCase() === "text".toUpperCase()) {
	    var s = this.textContent;
	    w = s.length * 6;
	  }
	  
	  return {
	    width : w,
	    height : 16
	  };  
	}
	
})();