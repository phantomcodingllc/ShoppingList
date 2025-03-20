package com.example.shoppinglist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


data class ShoppingItem(val id: Int,
                        var name: String,
                        var quantity: Int,
                        var isEditing: Boolean = false
)

@Composable
fun ShoppingListApp(){

    var showDialog by remember { mutableStateOf(false) }
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ){
        Button(
            onClick = {showDialog = true},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Item", color = Color.Black)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            items(sItems){ // list
                item ->
                if(item.isEditing){ // checks to see if item is being edited
                    // shopping item editor composable
                    ShoppingItemEditor(item = item, onEditComplete = { // what happens when we are done editing
                        editedName, editedQuantity -> // new variables for edited name and edited quantity

                        // List = mapping that list and copy it
                        sItems = sItems.map{ it.copy(isEditing = false) } // set item we are currently editing turns editing flag off


                        // new variable for the edited item
                        val editedItem = sItems.find{ it.id == item.id }
                        // item.id (the item we are currently editing)
                        // should = the it.id (item we are comparing it to) and when we find the item that has the same id
                        // store the item in editedItem


                        // editedItem?
                        editedItem?.let {// let function allows us to get the shopping item (editedItem)
                            // take the editedItem and change name
                            it.name = editedName
                            // take the editedItem and change quantity
                            it.quantity = editedQuantity
                        }
                    } )
                }else{

                    ShoppingListItem(item = item , onEditClick = {
                        // finding which item we are editing and changing its "isEditing" boolean to true
                        sItems = sItems.map{ it.copy(isEditing = it.id == item.id)}
                        // compare item id to shopping list element that is passed to us, if they
                        // are true, then editing is true
                    }, onDeleteClick = {
                        // deletes current item we are looking at from list
                        sItems = sItems - item
                    })
                }
                
            }
        }
    }


    if(showDialog){
        AlertDialog(onDismissRequest = { showDialog = false},
            confirmButton = {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(onClick = {

                                    if(itemName.isNotBlank()){
                                        val newItem = ShoppingItem(
                                            id = sItems.size+1,
                                            name = itemName,
                                            quantity = itemQuantity.toInt()
                                            )
                                        sItems = sItems + newItem
                                        showDialog = false
                                        itemName = ""
                                        itemQuantity = ""
                                    }

                                }) {
                                    Text("Add")
                                }
                                Button(onClick = {showDialog = false}) {
                                    Text("Cancel")
                                }
                            }

            },
            title = { Text("Add Shopping Item")},
            text= {
                    Column {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )

                        OutlinedTextField(
                            value = itemQuantity,
                            onValueChange = { itemQuantity = it },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
            }

            )


    }

}

@Composable
// when using this function, pass the (item:) you want to edit, and pass the string and int (onEditComplete: (String, Int) that you want to edit.
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete: (String, Int) -> Unit){


    var editedName by remember { mutableStateOf(item.name) }
    // App takes strings, not int. Change quantity from int to string or app will break
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }

    var isEditing by remember { mutableStateOf(item.isEditing) }



    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly)
    {
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = {editedName = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = {editedQuantity = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
        }

        Button(
            onClick = {
            isEditing = false
            // elvis operator, if editedQuantity is null, set to default value of 1
            onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)

            }
        ){
            Text("Save")
        }

    }

}


@Composable
fun ShoppingListItem(
    item: ShoppingItem,

    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,

){
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color.Blue),
                shape = RoundedCornerShape(20)
            ),
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.name, modifier = Modifier.padding(8.dp))
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))
        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }

            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)

            }

        }


    }
}

