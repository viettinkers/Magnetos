var numScenes = 0;

var allSceneEls = $('.scene');
var bodyEl = $('body');

bodyEl.ready(main);

function main() {
  numScenes = allSceneEls.length;
  bodyEl.click(function(evt) {
    var isNext = evt.clientX > bodyEl.width() / 3; 
    nextScene(isNext);
  });
  if (window.location.search) {
    var sceneIndexParam = window.location.search.split('=')[1];
    gotoScene(sceneIndexParam);
  }
}

function nextScene(isNext) {
  var currentIndex = allSceneEls.filter('.showing').index();
  var nextIndex = isNext ?
      (currentIndex + 1) % numScenes :
      (currentIndex + numScenes - 1) % numScenes;
  $(allSceneEls[currentIndex]).removeClass('showing');
  $(allSceneEls[nextIndex]).addClass('showing');
  // window.location.search = 'scene=' + nextIndex;
}

function gotoScene(sceneIndex) {
  allSceneEls.removeClass('showing');
  $(allSceneEls[sceneIndex]).addClass('showing');
}
