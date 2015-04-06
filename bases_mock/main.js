var numScenes = 0;

var allSceneEls = $('.scene');
var bodyEl = $('body');

function main() {
  numScenes = allSceneEls.length;
  bodyEl.click(function(evt) {
    var isNext = evt.clientX > bodyEl.width() / 3; 
    nextScene(isNext);
  });
}

function nextScene(isNext) {
  var currentIndex = allSceneEls.index('.showing');
  var nextIndex = isNext ?
      (currentIndex + 1) % numScenes :
      (currentIndex + numScenes - 1) % numScenes;
  $(allSceneEls[currentIndex]).removeClass('showing');
  $(allSceneEls[nextIndex]).addClass('showing');
}

main();