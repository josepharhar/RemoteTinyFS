// controls the history panel of the main page

polyFS_visualizer.history = {
   token: "",

   update_history: function(disk) {
      if (!disk){
         return;
      }
      
      var element = '<div>' +
                        '<button style=\'Margin: 5px\' onclick="polyFS_visualizer.load_disk.load.call(this, \'' + disk + '\', \'' + this.token + '\')">' +
                           disk + '<img src="static/hard-disk-icon.png" width="30px">' + 
                        '</button>' + 
                     '</div>';
      if( $("#disks li button:contains('" + disk + "')").length == 0){
         $("#disks").append('<li>' + element + '</li>');
      }
   }
}
