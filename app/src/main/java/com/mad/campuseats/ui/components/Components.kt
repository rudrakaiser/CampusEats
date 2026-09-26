package com.mad.campuseats.ui.components

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.mad.campuseats.data.FoodCategory
import com.mad.campuseats.data.FoodItem
import com.mad.campuseats.data.OrderStatus
import com.mad.campuseats.data.Restaurant
import com.mad.campuseats.data.coverPhotoUrl
import com.mad.campuseats.data.imageUrl
import com.mad.campuseats.navigation.Screen
import com.mad.campuseats.ui.theme.*

// ---------------------------------------------------------------- system bars / headers

/** Screens draw edge-to-edge; each one says whether the status-bar icons should be dark or light. */
@Composable
fun StatusBarStyle(darkIcons: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkIcons
        }
    }
}

/** Plain white top bar with optional back arrow. Handles the status-bar inset itself. */
@Composable
fun ScreenHeader(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    StatusBarStyle(darkIcons = true)
    Column(Modifier.fillMaxWidth().background(Color.White).statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Charcoal)
                }
            } else {
                Spacer(Modifier.width(16.dp))
            }
            Text(title, style = MaterialTheme.typography.titleLarge, color = Charcoal, modifier = Modifier.weight(1f))
            actions()
        }
        HorizontalDivider(color = Line)
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = Charcoal, modifier = modifier)
}

/** Round white icon button used over photos; becomes a plain icon button when [plain]. */
@Composable
fun RoundIconButton(
    icon: ImageVector,
    description: String,
    plain: Boolean,
    tint: Color = Charcoal,
    onClick: () -> Unit
) {
    if (plain) {
        IconButton(onClick = onClick) { Icon(icon, contentDescription = description, tint = tint) }
    } else {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(38.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = description, tint = tint, modifier = Modifier.size(20.dp))
        }
    }
}

// ---------------------------------------------------------------- inputs & buttons

@Composable
fun CampusSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search for food or restaurants",
    containerColor: Color = Sand,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = MutedText) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Pink) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = MutedText)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Pink
        )
    )
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    password: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Pink,
            focusedLabelColor = Pink,
            cursorColor = Pink,
            unfocusedBorderColor = Line
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/** Full-width pink pill. With [trailing] it becomes a "label ........ price" bar. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailing: String? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Pink,
            contentColor = Color.White,
            disabledContainerColor = Line,
            disabledContentColor = MutedText
        )
    ) {
        if (trailing == null) {
            Text(text, style = MaterialTheme.typography.titleSmall)
        } else {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text, style = MaterialTheme.typography.titleSmall)
                Text(trailing, style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
fun OutlinePillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Pink,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.5.dp, color)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(Modifier.width(8.dp))
        }
        Text(text, color = color, style = MaterialTheme.typography.titleSmall)
    }
}

// ---------------------------------------------------------------- home pieces

fun FoodCategory.icon(): ImageVector = when (this) {
    FoodCategory.MEALS -> Icons.Default.LunchDining
    FoodCategory.SNACKS -> Icons.Default.Fastfood
    FoodCategory.BEVERAGES -> Icons.Default.LocalCafe
    FoodCategory.DESSERTS -> Icons.Default.Cake
}

/** Rounded-square category shortcut with a label underneath. */
@Composable
fun CategoryTile(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(if (selected) PinkTint else Sand)
                .then(if (selected) Modifier.border(1.5.dp, Pink, RoundedCornerShape(18.dp)) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) Pink else Ink, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Pink else Charcoal,
            maxLines = 1
        )
    }
}

@Composable
fun NetworkImage(
    url: String,
    fallbackColor: Color,
    fallbackIcon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    // Shown while loading or when offline, so a slow/failed load never looks broken.
    val fallback: @Composable () -> Unit = {
        Box(
            modifier = Modifier.fillMaxSize().background(fallbackColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(fallbackIcon, contentDescription = null, tint = fallbackColor, modifier = Modifier.size(28.dp))
        }
    }
    Box(modifier = modifier) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true).build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = { fallback() },
            error = { fallback() }
        )
    }
}

@Composable
fun RestaurantCoverImage(restaurant: Restaurant, modifier: Modifier = Modifier) {
    NetworkImage(restaurant.coverPhotoUrl, restaurant.accentColor, Icons.Default.Restaurant, restaurant.name, modifier)
}

@Composable
fun FoodImage(item: FoodItem, accent: Color, modifier: Modifier = Modifier) {
    NetworkImage(item.imageUrl, accent, Icons.Default.Fastfood, item.name, modifier)
}

@Composable
fun HeartButton(isFavorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favourite",
            tint = if (isFavorite) Pink else Charcoal,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun RatingBadge(rating: Double, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Star, contentDescription = null, tint = Warning, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(2.dp))
        Text("$rating", style = MaterialTheme.typography.labelLarge, color = Charcoal)
        Text(" ($count)", style = MaterialTheme.typography.labelMedium, color = MutedText)
    }
}

fun deliveryFeeLabel(fee: Int): String = if (fee == 0) "Free delivery" else "৳$fee delivery"

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Box(modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(14.dp))) {
            RestaurantCoverImage(restaurant, Modifier.fillMaxSize())
            restaurant.promo?.let {
                Text(
                    it,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                        .background(Pink)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            HeartButton(isFavorite, onFavoriteClick, Modifier.align(Alignment.TopEnd).padding(10.dp))
            Text(
                restaurant.deliveryTime,
                style = MaterialTheme.typography.labelMedium,
                color = Charcoal,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(restaurant.name, style = MaterialTheme.typography.titleMedium, color = Charcoal, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            RatingBadge(restaurant.avgRating, restaurant.ratingCount)
        }
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = MutedText, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                "${deliveryFeeLabel(restaurant.deliveryFee)} · ${restaurant.tagline}",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PromoBanner(title: String, subtitle: String, icon: ImageVector, dark: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(270.dp)
            .height(104.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (dark) Charcoal else Pink)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.18f),
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp).size(84.dp)
        )
        Column(Modifier.align(Alignment.CenterStart).padding(start = 16.dp, end = 90.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
        }
    }
}

// ---------------------------------------------------------------- menu pieces

@Composable
fun MenuTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        label,
        style = MaterialTheme.typography.titleSmall,
        color = if (selected) Pink else Ink,
        modifier = Modifier
            .clickable { onClick() }
            .then(
                if (selected) Modifier.drawBehind {
                    val h = 3.dp.toPx()
                    drawRect(Pink, Offset(0f, size.height - h), Size(size.width, h))
                } else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    )
}

@Composable
fun QuantityStepper(
    quantity: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 32.dp
) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(50)).background(Pink).padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMinus, modifier = Modifier.size(buttonSize)) {
            Icon(
                if (quantity <= 1) Icons.Default.Delete else Icons.Default.Remove,
                contentDescription = "Remove one",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Text("$quantity", color = Color.White, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 4.dp))
        IconButton(onClick = onPlus, modifier = Modifier.size(buttonSize)) {
            Icon(Icons.Default.Add, contentDescription = "Add one", tint = Color.White, modifier = Modifier.size(18.dp))
        }
    }
}

/** One dish row: text on the left, photo on the right with a "+" (or stepper) over its corner. */
@Composable
fun MenuItemRow(
    item: FoodItem,
    accent: Color,
    quantityInCart: Int,
    onAdd: () -> Unit,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(item.name, style = MaterialTheme.typography.titleSmall, color = Charcoal)
            Spacer(Modifier.height(2.dp))
            Text(item.description, style = MaterialTheme.typography.bodySmall, color = MutedText, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))
            Text("৳${item.price}", style = MaterialTheme.typography.titleSmall, color = Charcoal)
        }
        Box(Modifier.size(100.dp)) {
            FoodImage(item, accent, Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)))
            Box(Modifier.align(Alignment.BottomEnd).padding(6.dp)) {
                if (quantityInCart == 0) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { onAdd() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add ${item.name}", tint = Pink)
                    }
                } else {
                    QuantityStepper(quantityInCart, onMinus, onPlus, buttonSize = 28.dp)
                }
            }
        }
    }
    HorizontalDivider(color = Line, modifier = Modifier.padding(start = 16.dp))
}

// ---------------------------------------------------------------- orders

@Composable
fun PillBadge(text: String, background: Color, contentColor: Color = Color.White) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = contentColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}

fun statusColor(status: OrderStatus): Color = when (status) {
    OrderStatus.PLACED -> MutedText
    OrderStatus.ACCEPTED -> Success
    OrderStatus.PREPARING -> Warning
    OrderStatus.READY -> Pink
    OrderStatus.DELIVERED -> Success
    OrderStatus.DECLINED -> Danger
}

@Composable
fun OrderStatusStepper(currentStatus: OrderStatus, modifier: Modifier = Modifier) {
    val steps = listOf(OrderStatus.PLACED, OrderStatus.ACCEPTED, OrderStatus.PREPARING, OrderStatus.READY, OrderStatus.DELIVERED)
    val currentIndex = steps.indexOf(currentStatus).coerceAtLeast(0)
    Column(modifier = modifier) {
        steps.forEachIndexed { index, step ->
            val done = index <= currentIndex
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Box(
                    modifier = Modifier.size(22.dp).clip(CircleShape).background(if (done) Pink else Line),
                    contentAlignment = Alignment.Center
                ) {
                    if (done) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    step.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (done) Charcoal else MutedText,
                    fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Normal
                )
            }
            if (index != steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .width(2.dp)
                        .height(14.dp)
                        .background(if (index < currentIndex) Pink else Line)
                )
            }
        }
    }
}

@Composable
fun StarRatingInput(rating: Int, onRatingChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Rate $i",
                tint = if (i <= rating) Warning else MutedText,
                modifier = Modifier
                    .size(38.dp)
                    .clickable { onRatingChange(i) }
                    .padding(3.dp)
            )
        }
    }
}

/** Stand-in for a Google Map: a simple street-grid drawing with a pin. */
@Composable
fun MapPlaceholder(locationLabel: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE8EEE4))
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val road = Color.White
            drawLine(road, Offset(0f, size.height * 0.35f), Offset(size.width, size.height * 0.62f), strokeWidth = 18f)
            drawLine(road, Offset(size.width * 0.3f, 0f), Offset(size.width * 0.48f, size.height), strokeWidth = 14f)
            drawLine(road, Offset(size.width * 0.78f, 0f), Offset(size.width * 0.7f, size.height), strokeWidth = 10f)
        }
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Pink, modifier = Modifier.size(38.dp))
            Text(
                locationLabel,
                style = MaterialTheme.typography.labelMedium,
                color = Charcoal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}

// ---------------------------------------------------------------- navigation

@Composable
fun BottomNavBar(currentRoute: String?, cartCount: Int, onNavigate: (String) -> Unit) {
    Column {
        HorizontalDivider(color = Line)
        NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
            val items = listOf(
                Triple(Screen.Home.route, Icons.Default.Home, "Home"),
                Triple(Screen.Cart.route, Icons.Default.ShoppingBag, "Cart"),
                Triple(Screen.Orders.route, Icons.Default.Receipt, "Orders"),
                Triple(Screen.Profile.route, Icons.Default.Person, "Account")
            )
            items.forEach { (route, icon, label) ->
                NavigationBarItem(
                    selected = currentRoute == route,
                    onClick = { onNavigate(route) },
                    icon = {
                        BadgedBox(badge = {
                            if (route == Screen.Cart.route && cartCount > 0) {
                                Badge(containerColor = Pink) { Text("$cartCount", color = Color.White) }
                            }
                        }) {
                            Icon(icon, contentDescription = label)
                        }
                    },
                    label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Pink,
                        selectedTextColor = Pink,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun EmptyState(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.size(84.dp).clip(CircleShape).background(PinkTint), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = Pink, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = Charcoal)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MutedText)
    }
}

/**
 * Side-drawer content so screens outside the bottom nav (restaurant page, ...) can jump straight
 * to Home / Cart / Orders / Account. The caller closes the drawer and navigates.
 */
@Composable
fun AppDrawerContent(cartCount: Int, onNavigate: (String) -> Unit) {
    ModalDrawerSheet(drawerContainerColor = Color.White) {
        Column(Modifier.fillMaxWidth().background(Pink).statusBarsPadding().padding(24.dp)) {
            Text("CampusEats", style = MaterialTheme.typography.headlineSmall, color = Color.White)
            Text("Premier University", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
        }
        Spacer(Modifier.height(8.dp))
        val drawerColors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
        NavigationDrawerItem(
            label = { Text("Browse restaurants") },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            selected = false,
            onClick = { onNavigate(Screen.Home.route) },
            colors = drawerColors,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        NavigationDrawerItem(
            label = { Text(if (cartCount > 0) "Cart ($cartCount)" else "Cart") },
            icon = { Icon(Icons.Default.ShoppingBag, contentDescription = null) },
            selected = false,
            onClick = { onNavigate(Screen.Cart.route) },
            colors = drawerColors,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        NavigationDrawerItem(
            label = { Text("Your orders") },
            icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
            selected = false,
            onClick = { onNavigate(Screen.Orders.route) },
            colors = drawerColors,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        NavigationDrawerItem(
            label = { Text("Account") },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            selected = false,
            onClick = { onNavigate(Screen.Profile.route) },
            colors = drawerColors,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

/** Shared layout for the three login/register screens: coloured hero on top, white sheet below. */
@Composable
fun AuthScaffold(
    heroColor: Color,
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    StatusBarStyle(darkIcons = false)
    Column(Modifier.fillMaxSize().background(heroColor)) {
        Column(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 24.dp).padding(bottom = 28.dp)
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            } else {
                Spacer(Modifier.height(36.dp))
            }
            Box(Modifier.size(56.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = Pink, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(24.dp),
            content = content
        )
    }
}
