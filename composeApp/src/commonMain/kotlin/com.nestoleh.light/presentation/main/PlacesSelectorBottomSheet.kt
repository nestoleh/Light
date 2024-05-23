package com.nestoleh.light.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nestoleh.light.domain.model.Place
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.button_add_place
import light.composeapp.generated.resources.ic_check
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlacesSelectorBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    selectedPlace: Place?,
    allPlaces: List<Place>,
    onPlaceSelected: (Place) -> Unit,
    onAddNewPlace: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        windowInsets = BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Bottom),
    ) {
        LazyColumn(
            modifier = modifier.fillMaxWidth().padding(
                start = 4.dp,
                end = 4.dp,
                bottom = 32.dp
            ),
        ) {
            items(allPlaces) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPlaceSelected(it) }
                        .padding(16.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f, true),
                        text = it.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (it.id == selectedPlace?.id) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_check),
                            contentDescription = "Selected item icon"
                        )
                    }
                }
            }
            item {
                Button(
                    modifier = Modifier
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    onClick = onAddNewPlace
                ) {
                    Text(text = stringResource(Res.string.button_add_place))
                }
            }
        }
    }

}